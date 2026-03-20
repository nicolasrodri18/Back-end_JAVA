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

/**
 * Servlet encargado de la gestión del inventario de productos.
 * Soporta operaciones de listado, creación, edición y eliminación (CRUD).
 * Todas las operaciones están restringidas por el estado de la relación laboral (Activo).
 */
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

    /**
     * Obtiene la lista de productos relacionados a una empresa específica en formato JSON.
     * Solo accesible si el usuario está Activo.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws IOException Si ocurre un error de escritura.
     */
    private void listarInventario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Obtiene el estado de la relación laboral almacenado en la sesión.
        String status = (String) session.getAttribute("statusRelacion");
        // Bloquea el acceso si la relación está marcada como 'Inactivo'.
        if ("Inactivo".equals(status)) {
            response.setStatus(403); // Responde con prohibido.
            out.print("{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva. No puedes acceder al almacén.\"}");
            return;
        }

        // Intenta obtener el NIT de la empresa desde la sesión (para empleados).
        String nitEmpresa = (String) session.getAttribute("ciaNit");
        // Si no existe, asume que el usuario actual es la empresa misma.
        if (nitEmpresa == null)
            nitEmpresa = (String) session.getAttribute("userDoc");

        // Si no se encuentra un NIT válido tras ambos intentos, devuelve lista vacía.
        if (nitEmpresa == null) {
            out.print("[]");
            return;
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            // Consulta SQL detallada que une el almacén con el maestro de productos.
            String sql = "SELECT A.Registro_Almacen, A.ID_Producto, P.Nombre_Producto, A.Stock, A.Precio_Asignado, " +
                    "A.Precio_de_Compra, A.Porcentaje_de_ganancia, A.Fecha_Vencimiento " +
                    "FROM TBL_Almacen A " +
                    "JOIN TBL_Producto P ON A.ID_Producto = P.ID_Producto " +
                    "WHERE A.DOCUMENTO_NIT = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nitEmpresa); // Inyecta el NIT para filtrar solo productos de esa empresa.
                try (ResultSet rs = ps.executeQuery()) {
                    out.print("["); // Inicia la construcción manual del array JSON.
                    boolean primero = true;
                    while (rs.next()) {
                        if (!primero)
                            out.print(","); // Añade coma entre objetos JSON.
                        out.print("{");
                        // Serialización manual de cada registro a formato JSON.
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
                    out.print("]"); // Cierra el array JSON.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al cargar inventario.\"}");
        }
    }

    /**
     * Procesa la adición de un nuevo producto al inventario.
     * Realiza validaciones de campos obligatorios, longitud de ID y fecha de vencimiento.
     * Utiliza una transacción para asegurar la integridad en TBL_Producto y TBL_Almacen.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws IOException Si ocurre un error de escritura.
     */
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

        // Se obtiene el NIT de la sesión del usuario (empresa logueada).
        String nitEmpresa = (String) session.getAttribute("userDoc");
        // Captura del ID del producto enviado por el formulario.
        String idStr = request.getParameter("id");
        // Validación técnica: El ID no debe superar la longitud de un INT estándar en la base de datos (9 dígitos).
        if (idStr != null && idStr.length() > 9) {
            out.print("{\"status\":\"error\", \"message\":\"El ID del producto no puede exceder los 9 dígitos.\"}");
            return;
        }
        int idProducto = Integer.parseInt(idStr); // Conversión a entero.
        String nombre = request.getParameter("nombre"); // Nombre comercial.
        int stock = Integer.parseInt(request.getParameter("stock")); // Cantidad inicial.
        double precio = Double.parseDouble(request.getParameter("precio")); // Valor de venta.

        // Campos obligatorios
        String precioCompraStr = request.getParameter("precioCompra");
        String porcentajeGananciaStr = request.getParameter("porcentajeGanancia");
        String fechaVencimientoStr = request.getParameter("fechaVencimiento");

        if (precioCompraStr == null || precioCompraStr.isEmpty() || fechaVencimientoStr == null || fechaVencimientoStr.isEmpty()) {
            out.print("{\"status\":\"error\", \"message\":\"Todos los campos son obligatorios, incluyendo el precio de compra y la fecha de vencimiento.\"}");
            return;
        }

        Double precioCompra = Double.parseDouble(precioCompraStr);
        Double porcentajeGanancia = (porcentajeGananciaStr != null && !porcentajeGananciaStr.isEmpty())
                ? Double.parseDouble(porcentajeGananciaStr)
                : null;
        java.sql.Date fechaVencimiento = java.sql.Date.valueOf(fechaVencimientoStr);

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
            // Desactiva el auto-commit para manejar manualmente la transacción y asegurar atomicidad.
            conn.setAutoCommit(false);
            try {
                // 1. Inserta el registro básico del producto en el maestro global.
                String sqlProd = "INSERT INTO TBL_Producto (ID_Producto, Nombre_Producto) VALUES (?, ?)";
                try (PreparedStatement psP2 = conn.prepareStatement(sqlProd)) {
                    psP2.setInt(1, idProducto);
                    psP2.setString(2, nombre);
                    psP2.executeUpdate();
                }

                // 2. Vincula el producto a la empresa específica en la tabla de almacén.
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

                // Si ambas inserciones fueron exitosas, confirma los cambios en la DB.
                conn.commit();
                out.print("{\"status\":\"success\", \"message\":\"Producto agregado correctamente.\"}");
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                // Si el ID ya existe, revierte cualquier cambio parcial realizado.
                conn.rollback();
                out.print("{\"status\":\"error\", \"message\":\"ID de producto ya registrado.\"}");
                return;
            } catch (Exception e) {
                // Ante cualquier otro error inesperado, revierte la transacción.
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al agregar producto: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Actualiza la información de un producto y su registro en el almacén.
     * Mantiene la integridad mediante una transacción SQL.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws IOException Si ocurre un error de escritura.
     */
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

        // Campos obligatorios
        String precioCompraStr = request.getParameter("precioCompra");
        String porcentajeGananciaStr = request.getParameter("porcentajeGanancia");
        String fechaVencimientoStr = request.getParameter("fechaVencimiento");

        if (precioCompraStr == null || precioCompraStr.isEmpty() || fechaVencimientoStr == null || fechaVencimientoStr.isEmpty()) {
            out.print("{\"status\":\"error\", \"message\":\"Todos los campos son obligatorios.\"}");
            return;
        }

        Double precioCompra = Double.parseDouble(precioCompraStr);
        Double porcentajeGanancia = (porcentajeGananciaStr != null && !porcentajeGananciaStr.isEmpty())
                ? Double.parseDouble(porcentajeGananciaStr)
                : null;
        java.sql.Date fechaVencimiento = java.sql.Date.valueOf(fechaVencimientoStr);

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
            // Manejo manual de transacción para actualizar dos tablas relacionadas.
            conn.setAutoCommit(false);
            try {
                // 1. Actualiza el nombre del producto en el catálogo maestro.
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

    /**
     * Elimina completamente un producto del almacén y del maestro de productos.
     * Se realiza una eliminación en cascada lógica dentro de una transacción.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws IOException Si ocurre un error de escritura.
     */
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
