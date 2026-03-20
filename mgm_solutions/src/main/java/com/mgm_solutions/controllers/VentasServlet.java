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
 * Servlet encargado de la gestión de transacciones de venta.
 * Permite registrar nuevas ventas actualizando el stock y consultar el historial por empresa.
 * Incluye lógica de reversión (eliminación) de ventas registradas en el día actual.
 */
@WebServlet("/VentasServlet")
public class VentasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("listarEmpresa".equals(action)) {
            listarVentasEmpresa(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("registrarVenta".equals(action)) {
            registrarVenta(request, response);
        } else if ("eliminar".equals(action)) {
            eliminarVenta(request, response);
        }
    }

    private void listarVentasEmpresa(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String nitEmpresa = (String) session.getAttribute("userDoc");

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (nitEmpresa == null) {
            out.print("[]");
            return;
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "SELECT V.ID_Registro_Venta, V.DOCUMENTO_NIT AS EmpleadoID, U.Nombre AS EmpleadoNombre, V.Fecha, V.Total_Venta, "
                    +
                    "P.Nombre_Producto, PV.Cantidad_Vendida, PV.Registro_Almacen, PV.Precio_Unitario_Venta " +
                    "FROM TBL_Venta V " +
                    "JOIN TBL_USUARIOS U ON V.DOCUMENTO_NIT = U.DOCUMENTO_NIT " +
                    "JOIN TBL_Productos_Vendidos PV ON V.ID_Registro_Venta = PV.ID_Registro_Venta " +
                    "JOIN TBL_Almacen A ON PV.Registro_Almacen = A.Registro_Almacen " +
                    "JOIN TBL_Producto P ON A.ID_Producto = P.ID_Producto " +
                    "WHERE A.DOCUMENTO_NIT = ? " +
                    "ORDER BY V.Fecha DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nitEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    out.print("[");
                    boolean primero = true;
                    while (rs.next()) {
                        if (!primero)
                            out.print(",");
                        out.print("{");
                        out.print("\"idVenta\":" + rs.getInt("ID_Registro_Venta") + ",");
                        out.print("\"empleadoId\":\"" + rs.getString("EmpleadoID") + "\",");
                        out.print("\"empleadoNombre\":\"" + rs.getString("EmpleadoNombre") + "\",");
                        out.print("\"fecha\":\"" + rs.getDate("Fecha") + "\",");
                        out.print("\"total\":" + rs.getBigDecimal("Total_Venta") + ",");
                        out.print("\"producto\":\"" + rs.getString("Nombre_Producto") + "\",");
                        out.print("\"cantidad\":" + rs.getInt("Cantidad_Vendida") + ",");
                        out.print("\"precioUnitario\":" + rs.getBigDecimal("Precio_Unitario_Venta"));
                        out.print("}");
                        primero = false;
                    }
                    out.print("]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al cargar ventas.\"}");
        }
    }

    /**
     * Registra una nueva venta realizada por un empleado.
     * Realiza las siguientes operaciones en una transacción atómica:
     * 1. Valida el stock actual y la fecha de vencimiento.
     * 2. Registra la venta en TBL_Venta.
     * 3. Registra el detalle del producto en TBL_Productos_Vendidos.
     * 4. Descuenta la cantidad vendida del stock en TBL_Almacen.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws IOException Si ocurre un error de escritura.
     */
    private void registrarVenta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String nitEmpleado = (String) session.getAttribute("userDoc");
        int idRegistroAlmacen = Integer.parseInt(request.getParameter("idRegistroAlmacen"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        double totalVenta = Double.parseDouble(request.getParameter("total"));
        double precioUnitario = Double.parseDouble(request.getParameter("precioUnitario"));

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (nitEmpleado == null) {
            response.setStatus(401);
            out.print("{\"status\":\"error\", \"message\":\"Sesión de empleado no encontrada.\"}");
            return;
        }

        String status = (String) session.getAttribute("statusRelacion");
        if ("Inactivo".equals(status)) {
            response.setStatus(403);
            out.print("{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva. No puedes realizar ventas.\"}");
            return;
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
                // 0. Validar Stock actual y Fecha de Vencimiento
                String sqlCheck = "SELECT Stock, Fecha_Vencimiento FROM TBL_Almacen WHERE Registro_Almacen = ?";
                try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                    psCheck.setInt(1, idRegistroAlmacen);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next()) {
                            int stockActual = rs.getInt("Stock");
                            java.sql.Date fechaVenc = rs.getDate("Fecha_Vencimiento");

                            // Validar Vencimiento
                            if (fechaVenc != null) {
                                java.time.LocalDate today = java.time.LocalDate.now();
                                if (fechaVenc.toLocalDate().isBefore(today)) {
                                    out.print(
                                            "{\"status\":\"error\", \"message\":\"No se puede vender un producto vencido.\"}");
                                    return;
                                }
                            }

                            // Validar Stock
                            if (stockActual <= 0) {
                                out.print("{\"status\":\"error\", \"message\":\"No hay stock disponible (VACÍO).\"}");
                                return;
                            }
                            if (stockActual < cantidad) {
                                out.print("{\"status\":\"error\", \"message\":\"Stock insuficiente. Disponible: "
                                        + stockActual + "\"}");
                                return;
                            }
                        } else {
                            out.print("{\"status\":\"error\", \"message\":\"Producto no encontrado en almacén.\"}");
                            return;
                        }
                    }
                }

                // 1. Insertar en TBL_Venta
                int idVenta = 0;
                String sqlVenta = "INSERT INTO TBL_Venta (DOCUMENTO_NIT, Fecha, Total_Venta) VALUES (?, CURDATE(), ?)";
                try (PreparedStatement psV = conn.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psV.setString(1, nitEmpleado);
                    psV.setDouble(2, totalVenta);
                    psV.executeUpdate();
                    try (ResultSet rs = psV.getGeneratedKeys()) {
                        if (rs.next())
                            idVenta = rs.getInt(1);
                    }
                }

                // 2. Insertar en TBL_Productos_Vendidos
                String sqlPV = "INSERT INTO TBL_Productos_Vendidos (ID_Registro_Venta, Registro_Almacen, Cantidad_Vendida, Precio_Unitario_Venta) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psPV = conn.prepareStatement(sqlPV)) {
                    psPV.setInt(1, idVenta);
                    psPV.setInt(2, idRegistroAlmacen);
                    psPV.setInt(3, cantidad);
                    psPV.setDouble(4, precioUnitario);
                    psPV.executeUpdate();
                }

                // 3. Actualizar Stock en TBL_Almacen
                String sqlUpdateStock = "UPDATE TBL_Almacen SET Stock = Stock - ? WHERE Registro_Almacen = ?";
                try (PreparedStatement psU = conn.prepareStatement(sqlUpdateStock)) {
                    psU.setInt(1, cantidad);
                    psU.setInt(2, idRegistroAlmacen);
                    psU.executeUpdate();
                }

                conn.commit();
                out.print("{\"status\":\"success\", \"message\":\"Venta registrada correctamente.\"}");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al registrar venta: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Elimina una venta registrada y restaura el stock al inventario.
     * Solo permite eliminar ventas realizadas en la fecha actual (Seguridad).
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws IOException Si ocurre un error de escritura.
     */
    private void eliminarVenta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idVentaStr = request.getParameter("id");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idVentaStr == null) {
            response.setStatus(400);
            out.print("{\"status\":\"error\", \"message\":\"ID de venta no proporcionado.\"}");
            return;
        }

        int idVenta = Integer.parseInt(idVentaStr);

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
                // 1. Verificar que la venta sea de HOY
                String sqlCheckDate = "SELECT Fecha FROM TBL_Venta WHERE ID_Registro_Venta = ?";
                try (PreparedStatement psCD = conn.prepareStatement(sqlCheckDate)) {
                    psCD.setInt(1, idVenta);
                    try (ResultSet rs = psCD.executeQuery()) {
                        if (rs.next()) {
                            java.sql.Date fechaVenta = rs.getDate("Fecha");
                            java.time.LocalDate today = java.time.LocalDate.now();
                            if (!fechaVenta.toLocalDate().equals(today)) {
                                out.print(
                                        "{\"status\":\"error\", \"message\":\"Solo se pueden eliminar ventas del día actual.\"}");
                                return;
                            }
                        } else {
                            out.print("{\"status\":\"error\", \"message\":\"Venta no encontrada.\"}");
                            return;
                        }
                    }
                }

                // 2. Obtener productos vinculados a la venta para restaurar stock
                String sqlGetPV = "SELECT Registro_Almacen, Cantidad_Vendida FROM TBL_Productos_Vendidos WHERE ID_Registro_Venta = ?";
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetPV)) {
                    psGet.setInt(1, idVenta);
                    try (ResultSet rs = psGet.executeQuery()) {
                        while (rs.next()) {
                            int regAlmacen = rs.getInt("Registro_Almacen");
                            int cantidad = rs.getInt("Cantidad_Vendida");

                            // Restaurar stock
                            String sqlRestore = "UPDATE TBL_Almacen SET Stock = Stock + ? WHERE Registro_Almacen = ?";
                            try (PreparedStatement psRes = conn.prepareStatement(sqlRestore)) {
                                psRes.setInt(1, cantidad);
                                psRes.setInt(2, regAlmacen);
                                psRes.executeUpdate();
                            }
                        }
                    }
                }

                // 3. Eliminar de TBL_Productos_Vendidos
                String sqlDelPV = "DELETE FROM TBL_Productos_Vendidos WHERE ID_Registro_Venta = ?";
                try (PreparedStatement psDelPV = conn.prepareStatement(sqlDelPV)) {
                    psDelPV.setInt(1, idVenta);
                    psDelPV.executeUpdate();
                }

                // 4. Eliminar de TBL_Venta
                String sqlDelV = "DELETE FROM TBL_Venta WHERE ID_Registro_Venta = ?";
                try (PreparedStatement psDelV = conn.prepareStatement(sqlDelV)) {
                    psDelV.setInt(1, idVenta);
                    psDelV.executeUpdate();
                }

                conn.commit();
                out.print(
                        "{\"status\":\"success\", \"message\":\"Venta eliminada y stock restaurado correctamente.\"}");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al eliminar venta: " + e.getMessage() + "\"}");
        }
    }
}
