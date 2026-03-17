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
import java.sql.SQLException;

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
        } else if ("listarCiudades".equals(action)) {
            listarCiudades(request, response);
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
        } else if ("actualizarPerfil".equals(action)) {
            actualizarPerfil(request, response);
        } else if ("actualizarPerfilEmpresa".equals(action)) {
            actualizarPerfilEmpresa(request, response);
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

        String userName = (String) session.getAttribute("userName");
        String userEmail = (String) session.getAttribute("userEmail");
        String userDirec = (String) session.getAttribute("userDirec");
        Object rolObj = session.getAttribute("userRol");
        String userRol = (rolObj != null) ? String.valueOf(rolObj) : null;

        out.print("{");
        out.print("\"userName\":" + (userName != null ? "\"" + userName + "\"" : "null") + ",");
        out.print("\"userDoc\":" + (userDoc != null ? "\"" + userDoc + "\"" : "null") + ",");
        out.print("\"userEmail\":" + (userEmail != null ? "\"" + userEmail + "\"" : "null") + ",");
        out.print("\"userDirec\":" + (userDirec != null ? "\"" + userDirec + "\"" : "null") + ",");
        out.print("\"userRol\":" + (userRol != null ? "\"" + userRol + "\"" : "null") + ",");
        out.print("\"statusRelacion\":" + (statusRelacion != null ? "\"" + statusRelacion + "\"" : "null") + ",");
        out.print("\"nombreEmpresa\":" + (nombreEmpresa != null ? "\"" + nombreEmpresa + "\"" : "null") + ",");
        out.print("\"ciaNit\":" + (session.getAttribute("ciaNit") != null ? "\"" + session.getAttribute("ciaNit") + "\"" : "null") + ",");
        out.print("\"ciaEmail\":" + (session.getAttribute("ciaEmail") != null ? "\"" + session.getAttribute("ciaEmail") + "\"" : "null") + ",");
        out.print("\"ciaDirec\":" + (session.getAttribute("ciaDirec") != null ? "\"" + session.getAttribute("ciaDirec") + "\"" : "null") + ",");
        out.print("\"ciaCiudad\":" + (session.getAttribute("ciaCiudad") != null ? "\"" + session.getAttribute("ciaCiudad") + "\"" : "null"));
        out.print("}");
    }

    private void actualizarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String userDoc = (String) session.getAttribute("userDoc");

        if (userDoc == null) {
            out.print("{\"status\":\"error\", \"message\":\"Sesión expirada.\"}");
            return;
        }

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String direccion = request.getParameter("direccion");
        String pass = request.getParameter("pass");

        Connection conn = null;
        try {
            conn = ConnectionDB.gConnectionDB();
            conn.setAutoCommit(false);

            String sqlUser = "UPDATE TBL_USUARIOS SET Nombre = ?, Direccion = ? WHERE DOCUMENTO_NIT = ?";
            if (pass != null && !pass.trim().isEmpty()) {
                sqlUser = "UPDATE TBL_USUARIOS SET Nombre = ?, Direccion = ?, Contraseña = ? WHERE DOCUMENTO_NIT = ?";
            }

            try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                psUser.setString(1, nombre);
                psUser.setString(2, direccion);
                if (pass != null && !pass.trim().isEmpty()) {
                    psUser.setString(3, com.mgm_solutions.config.SecurityUtils.hashPassword(pass));
                    psUser.setString(4, userDoc);
                } else {
                    psUser.setString(3, userDoc);
                }
                psUser.executeUpdate();
            }

            String sqlEmail = "UPDATE TBL_Correos SET Correo = ? WHERE DOCUMENTO_NIT = ?";
            try (PreparedStatement psEmail = conn.prepareStatement(sqlEmail)) {
                psEmail.setString(1, email);
                psEmail.setString(2, userDoc);
                psEmail.executeUpdate();
            }

            conn.commit();

            session.setAttribute("userName", nombre);
            session.setAttribute("userEmail", email);
            session.setAttribute("userDirec", direccion);

            out.print("{\"status\":\"success\", \"message\":\"Perfil actualizado correctamente.\"}");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Error en la base de datos.\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
        }
    }

    private void actualizarPerfilEmpresa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String userDoc = (String) session.getAttribute("userDoc");

        if (userDoc == null) {
            out.print("{\"status\":\"error\", \"message\":\"Sesión expirada.\"}");
            return;
        }

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String direccion = request.getParameter("direccion");
        String ciudadId = request.getParameter("ciudad");
        String pass = request.getParameter("pass");

        Connection conn = null;
        try {
            conn = ConnectionDB.gConnectionDB();
            conn.setAutoCommit(false);

            String sqlUser = "UPDATE TBL_USUARIOS SET Nombre = ?, Direccion = ?, Ciudad = ? WHERE DOCUMENTO_NIT = ?";
            if (pass != null && !pass.trim().isEmpty()) {
                sqlUser = "UPDATE TBL_USUARIOS SET Nombre = ?, Direccion = ?, Ciudad = ?, Contraseña = ? WHERE DOCUMENTO_NIT = ?";
            }

            try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                psUser.setString(1, nombre);
                psUser.setString(2, direccion);
                psUser.setInt(3, Integer.parseInt(ciudadId));
                if (pass != null && !pass.trim().isEmpty()) {
                    psUser.setString(4, com.mgm_solutions.config.SecurityUtils.hashPassword(pass));
                    psUser.setString(5, userDoc);
                } else {
                    psUser.setString(4, userDoc);
                }
                psUser.executeUpdate();
            }

            String sqlCheckEmail = "SELECT Correo FROM TBL_Correos WHERE DOCUMENTO_NIT = ?";
            boolean exists = false;
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckEmail)) {
                psCheck.setString(1, userDoc);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) exists = true;
                }
            }

            if (exists) {
                String sqlEmail = "UPDATE TBL_Correos SET Correo = ? WHERE DOCUMENTO_NIT = ?";
                try (PreparedStatement psEmail = conn.prepareStatement(sqlEmail)) {
                    psEmail.setString(1, email);
                    psEmail.setString(2, userDoc);
                    psEmail.executeUpdate();
                }
            } else {
                String sqlEmail = "INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES (?, ?)";
                try (PreparedStatement psEmail = conn.prepareStatement(sqlEmail)) {
                    psEmail.setString(1, email);
                    psEmail.setString(2, userDoc);
                    psEmail.executeUpdate();
                }
            }

            conn.commit();

            // Refresh session attributes
            session.setAttribute("userName", nombre);
            session.setAttribute("userEmail", email);
            session.setAttribute("userDirec", direccion);
            
            // Get city name for session
            String sqlCity = "SELECT Nombre FROM TBL_CIUDADES WHERE ID_Ciudad = ?";
            try (PreparedStatement psCity = conn.prepareStatement(sqlCity)) {
                psCity.setInt(1, Integer.parseInt(ciudadId));
                try (ResultSet rsCity = psCity.executeQuery()) {
                    if (rsCity.next()) {
                        session.setAttribute("ciaCiudad", rsCity.getString("Nombre"));
                    }
                }
            }

            out.print("{\"status\":\"success\", \"message\":\"Datos de empresa actualizados correctamente.\"}");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Error en la base de datos.\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void listarCiudades(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        com.mgm_solutions.dao.CiudadDAO cityDao = new com.mgm_solutions.dao.CiudadDAO();
        java.util.List<com.mgm_solutions.models.Ciudad> ciudades = cityDao.listar();
        
        out.print("[");
        for (int i = 0; i < ciudades.size(); i++) {
            com.mgm_solutions.models.Ciudad c = ciudades.get(i);
            out.print("{\"id\":" + c.getId() + ", \"nombre\":\"" + c.getNombre() + "\"}");
            if (i < ciudades.size() - 1) out.print(",");
        }
        out.print("]");
    }
}
