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
            String sql = "SELECT A.Registro_Almacen, P.Nombre_Producto, A.Stock, A.Precio_Asignado " +
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
                        out.print("\"id\":\"" + rs.getInt("Registro_Almacen") + "\",");
                        out.print("\"nombre\":\"" + rs.getString("Nombre_Producto") + "\",");
                        out.print("\"stock\":" + rs.getInt("Stock") + ",");
                        out.print("\"precio\":" + rs.getBigDecimal("Precio_Asignado"));
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
        int idProducto = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        int stock = Integer.parseInt(request.getParameter("stock"));
        double precio = Double.parseDouble(request.getParameter("precio"));

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            conn.setAutoCommit(false);
            try {
                String sqlProd = "INSERT INTO TBL_Producto (ID_Producto, Nombre_Producto) VALUES (?, ?)";
                try (PreparedStatement psP2 = conn.prepareStatement(sqlProd)) {
                    psP2.setInt(1, idProducto);
                    psP2.setString(2, nombre);
                    psP2.executeUpdate();
                }

                String sqlAlm = "INSERT INTO TBL_Almacen (DOCUMENTO_NIT, ID_Producto, Precio_Asignado, Stock) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psA = conn.prepareStatement(sqlAlm)) {
                    psA.setString(1, nitEmpresa);
                    psA.setInt(2, idProducto);
                    psA.setDouble(3, precio);
                    psA.setInt(4, stock);
                    psA.executeUpdate();
                }

                conn.commit();
                out.print("{\"status\":\"success\", \"message\":\"Producto agregado correctamente.\"}");
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

    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String status = (String) session.getAttribute("statusRelacion");
        if ("Inactivo".equals(status)) {
            response.setStatus(403);
            out.print(
                    "{\"status\":\"error\", \"message\":\"Tu cuenta está inactiva. No puedes realizar esta acción.\"}");
            return;
        }

        int idRegistro = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "DELETE FROM TBL_Almacen WHERE Registro_Almacen = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idRegistro);
                int result = ps.executeUpdate();
                if (result > 0) {
                    out.print("{\"status\":\"success\", \"message\":\"Producto eliminado correctamente.\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"No se encontró el registro.\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"Error al eliminar producto.\"}");
        }
    }
}
