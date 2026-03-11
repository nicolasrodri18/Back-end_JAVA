package com.mgm_solutions.controllers;

import com.mgm_solutions.config.ConnectionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/InventarioServlet")
public class InventarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("listar".equals(action)) {
            listarInventario(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("agregar".equals(action)) {
            agregarProducto(request, response);
        } else if ("eliminar".equals(action)) {
            eliminarProducto(request, response);
        } else if ("editar".equals(action)) {
            editarProducto(request, response);
        }
    }

    private void listarInventario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String status = (String) session.getAttribute("statusRelacion");
        if ("Inactivo".equals(status)) {
            response.setStatus(403);
            out.print("{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva. No puedes acceder al almacén.\"}");
            return;
        }

        String nitEmpresa = (String) session.getAttribute("ciaNit");
        if (nitEmpresa == null)
            nitEmpresa = (String) session.getAttribute("userDoc");

        if (nitEmpresa == null) {
            out.print("[]");
            return;
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "SELECT A.Registro_Almacen, A.ID_Producto, P.Nombre_Producto, A.Stock, A.Precio_Asignado, " +
                    "A.Precio_de_Compra, A.Porcentaje_de_ganancia, A.Fecha_Vencimiento " +
                    "FROM TBL_Almacen A " +
                    "JOIN TBL_Producto P ON A.ID_Producto = P.ID_Producto " +
                    "WHERE A.DOCUMENTO_NIT = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nitEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    out.print("[");
                    boolean primero = true;
                    while (rs.next()) {
                        if (!primero)
                            out.print(",");
                        out.print("{");
                        out.print("\"idRegistro\":" + rs.getInt("Registro_Almacen") + ",");
                        out.print("\"idProducto\":" + rs.getInt("ID_Producto") + ",");
                        out.print("\"nombre\":\"" + rs.getString("Nombre_Producto") + "\",");
                        out.print("\"stock\":" + rs.getInt("Stock") + ",");
                        out.print("\"precio\":" + rs.getBigDecimal("Precio_Asignado") + ",");
                        out.print("\"precioCompra\":" + rs.getBigDecimal("Precio_de_Compra") + ",");
                        out.print("\"ganancia\":" + rs.getBigDecimal("Porcentaje_de_ganancia") + ",");
                        out.print("\"vencimiento\":\"" + rs.getDate("Fecha_Vencimiento") + "\"");
                        out.print("}");
                        primero = false;
                    }
                    out.print("]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al cargar inventario.\"}");
        }
    }

    private void agregarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String status = (String) session.getAttribute("statusRelacion");
        if ("Inactivo".equals(status)) {
            response.setStatus(403);
            out.print("{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva. No puedes agregar productos.\"}");
            return;
        }

        String nitEmpresa = (String) session.getAttribute("userDoc");
        String idStr = request.getParameter("id");
        if (idStr != null && idStr.length() > 9) {
            out.print("{\"status\":\"error\", \"message\":\"El ID del producto no puede exceder los 9 dígitos.\"}");
            return;
        }
        int idProducto = Integer.parseInt(idStr);
        String nombre = request.getParameter("nombre");
        int stock = Integer.parseInt(request.getParameter("stock"));
        double precio = Double.parseDouble(request.getParameter("precio"));

        // Nuevos campos opcionales
        String precioCompraStr = request.getParameter("precioCompra");
        String porcentajeGananciaStr = request.getParameter("porcentajeGanancia");
        String fechaVencimientoStr = request.getParameter("fechaVencimiento");

        Double precioCompra = (precioCompraStr != null && !precioCompraStr.isEmpty())
                ? Double.parseDouble(precioCompraStr)
                : null;
        Double porcentajeGanancia = (porcentajeGananciaStr != null && !porcentajeGananciaStr.isEmpty())
                ? Double.parseDouble(porcentajeGananciaStr)
                : null;
        java.sql.Date fechaVencimiento = (fechaVencimientoStr != null && !fechaVencimientoStr.isEmpty())
                ? java.sql.Date.valueOf(fechaVencimientoStr)
                : null;

        // Validar que la fecha de vencimiento no sea anterior a hoy
        if (fechaVencimiento != null) {
            java.time.LocalDate today = java.time.LocalDate.now();
            if (fechaVencimiento.toLocalDate().isBefore(today)) {
                out.print(
                        "{\"status\":\"error\", \"message\":\"La fecha de vencimiento no puede ser anterior a la fecha actual.\"}");
                return;
            }
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
                String sqlProd = "INSERT INTO TBL_Producto (ID_Producto, Nombre_Producto) VALUES (?, ?)";
                try (PreparedStatement psP2 = conn.prepareStatement(sqlProd)) {
                    psP2.setInt(1, idProducto);
                    psP2.setString(2, nombre);
                    psP2.executeUpdate();
                }

                String sqlAlm = "INSERT INTO TBL_Almacen (DOCUMENTO_NIT, ID_Producto, Precio_Asignado, Precio_de_Compra, Porcentaje_de_ganancia, Stock, Fecha_Vencimiento) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psA = conn.prepareStatement(sqlAlm)) {
                    psA.setString(1, nitEmpresa);
                    psA.setInt(2, idProducto);
                    psA.setDouble(3, precio);
                    if (precioCompra != null) {
                        psA.setDouble(4, precioCompra);
                    } else {
                        psA.setNull(4, java.sql.Types.DECIMAL);
                    }
                    if (porcentajeGanancia != null) {
                        psA.setDouble(5, porcentajeGanancia);
                    } else {
                        psA.setNull(5, java.sql.Types.DECIMAL);
                    }
                    psA.setInt(6, stock);
                    if (fechaVencimiento != null) {
                        psA.setDate(7, fechaVencimiento);
                    } else {
                        psA.setNull(7, java.sql.Types.DATE);
                    }
                    psA.executeUpdate();
                }

                conn.commit();
                out.print("{\"status\":\"success\", \"message\":\"Producto agregado correctamente.\"}");
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                conn.rollback();
                out.print("{\"status\":\"error\", \"message\":\"ID de producto ya registrado.\"}");
                return;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al agregar producto: " + e.getMessage() + "\"}");
        }
    }

    private void editarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String status = (String) session.getAttribute("statusRelacion");
        if ("Inactivo".equals(status)) {
            response.setStatus(403);
            out.print("{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva.\"}");
            return;
        }

        int idRegistro = Integer.parseInt(request.getParameter("idRegistro"));
        int idProducto = Integer.parseInt(request.getParameter("idProducto"));
        String nombre = request.getParameter("nombre");
        int stock = Integer.parseInt(request.getParameter("stock"));
        double precio = Double.parseDouble(request.getParameter("precio"));

        String precioCompraStr = request.getParameter("precioCompra");
        String porcentajeGananciaStr = request.getParameter("porcentajeGanancia");
        String fechaVencimientoStr = request.getParameter("fechaVencimiento");

        Double precioCompra = (precioCompraStr != null && !precioCompraStr.isEmpty())
                ? Double.parseDouble(precioCompraStr)
                : null;
        Double porcentajeGanancia = (porcentajeGananciaStr != null && !porcentajeGananciaStr.isEmpty())
                ? Double.parseDouble(porcentajeGananciaStr)
                : null;
        java.sql.Date fechaVencimiento = (fechaVencimientoStr != null && !fechaVencimientoStr.isEmpty())
                ? java.sql.Date.valueOf(fechaVencimientoStr)
                : null;

        // Validar que la fecha de vencimiento no sea anterior a hoy (solo si se cambia)
        // Se comenta o elimina la validación en edición para permitir corregir
        // stock/nombre de productos ya vencidos
        /*
         * if (fechaVencimiento != null) {
         * java.time.LocalDate today = java.time.LocalDate.now();
         * if (fechaVencimiento.toLocalDate().isBefore(today)) {
         * out.print(
         * "{\"status\":\"error\", \"message\":\"La fecha de vencimiento no puede ser anterior a la fecha actual.\"}"
         * );
         * return;
         * }
         * }
         */

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
                // 1. Actualizar Nombre en TBL_Producto
                String sqlProd = "UPDATE TBL_Producto SET Nombre_Producto = ? WHERE ID_Producto = ?";
                try (PreparedStatement psP = conn.prepareStatement(sqlProd)) {
                    psP.setString(1, nombre);
                    psP.setInt(2, idProducto);
                    psP.executeUpdate();
                }

                // 2. Actualizar TBL_Almacen
                String sqlAlm = "UPDATE TBL_Almacen SET Precio_Asignado = ?, Precio_de_Compra = ?, Porcentaje_de_ganancia = ?, Stock = ?, Fecha_Vencimiento = ? WHERE Registro_Almacen = ?";
                try (PreparedStatement psA = conn.prepareStatement(sqlAlm)) {
                    psA.setDouble(1, precio);
                    if (precioCompra != null)
                        psA.setDouble(2, precioCompra);
                    else
                        psA.setNull(2, java.sql.Types.DECIMAL);
                    if (porcentajeGanancia != null)
                        psA.setDouble(3, porcentajeGanancia);
                    else
                        psA.setNull(3, java.sql.Types.DECIMAL);
                    psA.setInt(4, stock);
                    if (fechaVencimiento != null)
                        psA.setDate(5, fechaVencimiento);
                    else
                        psA.setNull(5, java.sql.Types.DATE);
                    psA.setInt(6, idRegistro);
                    psA.executeUpdate();
                }

                conn.commit();
                out.print("{\"status\":\"success\", \"message\":\"Producto actualizado correctamente.\"}");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al editar producto: " + e.getMessage() + "\"}");
        }
    }

    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String status = (String) session.getAttribute("statusRelacion");
        if ("Inactivo".equals(status)) {
            response.setStatus(403);
            out.print("{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva.\"}");
            return;
        }

        int idRegistro = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
                // Obtener ID_Producto antes de borrar del almacén
                int idProductToDel = -1;
                String sqlGetId = "SELECT ID_Producto FROM TBL_Almacen WHERE Registro_Almacen = ?";
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetId)) {
                    psGet.setInt(1, idRegistro);
                    try (ResultSet rs = psGet.executeQuery()) {
                        if (rs.next())
                            idProductToDel = rs.getInt("ID_Producto");
                    }
                }

                // 1. Borrar de TBL_Almacen
                String sqlAlm = "DELETE FROM TBL_Almacen WHERE Registro_Almacen = ?";
                try (PreparedStatement psA = conn.prepareStatement(sqlAlm)) {
                    psA.setInt(1, idRegistro);
                    psA.executeUpdate();
                }

                // 2. Borrar de TBL_Producto (si existe)
                if (idProductToDel != -1) {
                    String sqlProd = "DELETE FROM TBL_Producto WHERE ID_Producto = ?";
                    try (PreparedStatement psP = conn.prepareStatement(sqlProd)) {
                        psP.setInt(1, idProductToDel);
                        psP.executeUpdate();
                    }
                }

                conn.commit();
                out.print("{\"status\":\"success\", \"message\":\"Producto eliminado totalmente.\"}");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al eliminar producto.\"}");
        }
    }
}
