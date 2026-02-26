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
            String sql = "SELECT V.ID_Registro_Venta, V.DOCUMENTO_NIT AS EmpleadoID, V.Fecha, V.Total_Venta, " +
                    "P.Nombre_Producto, PV.Cantidad_Vendida, PV.Registro_Almacen " +
                    "FROM TBL_Venta V " +
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
                        out.print("\"fecha\":\"" + rs.getDate("Fecha") + "\",");
                        out.print("\"total\":" + rs.getBigDecimal("Total_Venta") + ",");
                        out.print("\"producto\":\"" + rs.getString("Nombre_Producto") + "\",");
                        out.print("\"cantidad\":" + rs.getInt("Cantidad_Vendida"));
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

    private void registrarVenta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String nitEmpleado = (String) session.getAttribute("userDoc");
        int idRegistroAlmacen = Integer.parseInt(request.getParameter("idRegistroAlmacen"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        double totalVenta = Double.parseDouble(request.getParameter("total"));

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (nitEmpleado == null) {
            response.setStatus(401);
            out.print("{\"status\":\"error\", \"message\":\"Sesión de empleado no encontrada.\"}");
            return;
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
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
                String sqlPV = "INSERT INTO TBL_Productos_Vendidos (ID_Registro_Venta, Registro_Almacen, Cantidad_Vendida) VALUES (?, ?, ?)";
                try (PreparedStatement psPV = conn.prepareStatement(sqlPV)) {
                    psPV.setInt(1, idVenta);
                    psPV.setInt(2, idRegistroAlmacen);
                    psPV.setInt(3, cantidad);
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
}
