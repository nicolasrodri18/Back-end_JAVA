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

@WebServlet("/RelacionLaboralServlet")
public class RelacionLaboralServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("buscarUsuario".equals(action)) {
            buscarUsuario(request, response);
        } else if ("listarEmpleados".equals(action)) {
            listarEmpleados(request, response);
        } else if ("getPerfil".equals(action)) {
            getPerfil(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("invitarEmpleado".equals(action)) {
            invitarEmpleado(request, response);
        } else if ("aceptarInvitacion".equals(action)) {
            aceptarInvitacion(request, response);
        } else if ("eliminarRelacion".equals(action)) {
            eliminarRelacion(request, response);
        } else if ("actualizarEstado".equals(action)) {
            actualizarEstado(request, response);
        }
    }

    private void buscarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String documento = request.getParameter("documento");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "SELECT U.DOCUMENTO_NIT, U.Nombre, C.Correo " +
                    "FROM TBL_USUARIOS U " +
                    "LEFT JOIN TBL_Correos C ON U.DOCUMENTO_NIT = C.DOCUMENTO_NIT " +
                    "WHERE U.DOCUMENTO_NIT = ? AND U.ID_ROL = 3"; // Solo rol Usuario/Empleado
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, documento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        out.print("{\"status\":\"success\", \"nombre\":\"" + rs.getString("Nombre") +
                                "\", \"correo\":\"" + (rs.getString("Correo") != null ? rs.getString("Correo") : "") +
                                "\", \"documento\":\"" + rs.getString("DOCUMENTO_NIT") + "\"}");
                    } else {
                        out.print("{\"status\":\"error\", \"message\":\"Usuario no encontrado o no es un empleado.\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Error de servidor.\"}");
        }
    }

    private void invitarEmpleado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String nitEmpresa = (String) session.getAttribute("userDoc"); // El NIT de la empresa lo sacamos de la sesión
        String nitEmpleado = request.getParameter("nitEmpleado");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (nitEmpresa == null) {
            out.print("{\"status\":\"error\", \"message\":\"Sesión de empresa no válida.\"}");
            return;
        }

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            // Verificar si ya existe una relación
            String sqlCheck = "SELECT ID_Relacion FROM TBL_RELACION_LABORAL WHERE NIT_EMPLEADO = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setString(1, nitEmpleado);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        out.print(
                                "{\"status\":\"error\", \"message\":\"El empleado ya se encuentra vinculado a una empresa.\"}");
                        return;
                    }
                }
            }

            String sql = "INSERT INTO TBL_RELACION_LABORAL (NIT_EMPLEADO, NIT_EMPRESA, Estado) VALUES (?, ?, 'Activo')";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nitEmpleado);
                ps.setString(2, nitEmpresa);
                ps.executeUpdate();
                out.print("{\"status\":\"success\", \"message\":\"Empleado agregado correctamente.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Error al enviar invitación.\"}");
        }
    }

    private void aceptarInvitacion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String nitEmpleado = (String) session.getAttribute("userDoc");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "UPDATE TBL_RELACION_LABORAL SET Estado = 'Activo' WHERE NIT_EMPLEADO = ? AND Estado = 'Pendiente'";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nitEmpleado);
                int fila = ps.executeUpdate();
                if (fila > 0) {
                    out.print("{\"status\":\"success\", \"message\":\"Invitación aceptada.\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"No hay invitaciones pendientes.\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Error al aceptar invitación.\"}");
        }
    }

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String nitEmpresa = (String) session.getAttribute("userDoc");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "SELECT R.ID_Relacion, U.DOCUMENTO_NIT, U.Nombre, C.Correo, R.Estado, U.Direccion " +
                    "FROM TBL_RELACION_LABORAL R " +
                    "JOIN TBL_USUARIOS U ON R.NIT_EMPLEADO = U.DOCUMENTO_NIT " +
                    "LEFT JOIN TBL_Correos C ON U.DOCUMENTO_NIT = C.DOCUMENTO_NIT " +
                    "WHERE R.NIT_EMPRESA = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nitEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    out.print("[");
                    boolean primero = true;
                    while (rs.next()) {
                        if (!primero)
                            out.print(",");
                        out.print("{");
                        out.print("\"id\":\"" + rs.getInt("ID_Relacion") + "\",");
                        out.print("\"documento\":\"" + rs.getString("DOCUMENTO_NIT") + "\",");
                        out.print("\"nombre\":\"" + rs.getString("Nombre") + "\",");
                        out.print("\"correo\":\"" + rs.getString("Correo") + "\",");
                        out.print("\"estado\":\"" + rs.getString("Estado") + "\",");
                        out.print("\"direccion\":\""
                                + (rs.getString("Direccion") != null ? rs.getString("Direccion") : "") + "\"");
                        out.print("}");
                        primero = false;
                    }
                    out.print("]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    private void getPerfil(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String userDoc = (String) session.getAttribute("userDoc");
        String statusRelacion = (String) session.getAttribute("statusRelacion");

        // Refresh status from DB to ensure it's up to date
        if (userDoc != null) {
            try (Connection conn = ConnectionDB.gConnectionDB()) {
                String sql = "SELECT Estado FROM TBL_RELACION_LABORAL WHERE NIT_EMPLEADO = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, userDoc);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            statusRelacion = rs.getString("Estado");
                            session.setAttribute("statusRelacion", statusRelacion);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (statusRelacion == null || statusRelacion.isEmpty()) {
            statusRelacion = "Disponible";
        }

        String nombreEmpresa = (String) session.getAttribute("nombreEmpresa");

        out.print("{");
        out.print("\"userName\":\"" + session.getAttribute("userName") + "\",");
        out.print("\"userDoc\":\"" + userDoc + "\",");
        out.print("\"userEmail\":\"" + session.getAttribute("userEmail") + "\",");
        out.print("\"userDirec\":\"" + session.getAttribute("userDirec") + "\",");
        out.print("\"userRol\":\"" + session.getAttribute("userRol") + "\",");
        out.print("\"statusRelacion\":\"" + statusRelacion + "\",");
        out.print("\"nombreEmpresa\":" + (nombreEmpresa != null ? "\"" + nombreEmpresa + "\"" : "null") + ",");
        out.print("\"ciaNit\":\"" + session.getAttribute("ciaNit") + "\",");
        out.print("\"ciaEmail\":\"" + session.getAttribute("ciaEmail") + "\",");
        out.print("\"ciaDirec\":\"" + session.getAttribute("ciaDirec") + "\",");
        out.print("\"ciaCiudad\":\"" + session.getAttribute("ciaCiudad") + "\"");
        out.print("}");
    }

    private void eliminarRelacion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idRelacion = request.getParameter("idRelacion");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "DELETE FROM TBL_RELACION_LABORAL WHERE ID_Relacion = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, idRelacion);
                ps.executeUpdate();
                out.print("{\"status\":\"success\", \"message\":\"Relación eliminada.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\"}");
        }
    }

    private void actualizarEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idRelacion = request.getParameter("idRelacion");
        String nuevoEstado = request.getParameter("estado");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionDB.gConnectionDB()) {
            String sql = "UPDATE TBL_RELACION_LABORAL SET Estado = ? WHERE ID_Relacion = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setString(2, idRelacion);
                int result = ps.executeUpdate();
                if (result > 0) {
                    out.print("{\"status\":\"success\", \"message\":\"Estado actualizado correctamente.\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"No se pudo actualizar el estado.\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Error de servidor.\"}");
        }
    }
}
