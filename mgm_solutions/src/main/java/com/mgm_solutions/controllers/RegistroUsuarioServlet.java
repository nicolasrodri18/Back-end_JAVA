package com.mgm_solutions.controllers;

import com.mgm_solutions.config.ConnectionDB;
import com.mgm_solutions.config.SecurityUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet que gestiona el registro de usuarios individuales (clientes/empleados).
 * Asocia automáticamente el rol de Usuario (3) y una ciudad por defecto.
 */
@WebServlet("/RegistroUsuarioServlet")
public class RegistroUsuarioServlet extends HttpServlet {

    /**
     * Procesa el formulario de registro de usuario.
     * Verifica que el documento y el correo no estén registrados previamente.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws ServletException Si ocurre un error de servlet.
     * @throws IOException Si ocurre un error de red.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        // 1. Recibir datos del formulario
        String nombre = request.getParameter("nombre-completo");
        String nit = request.getParameter("nit-documento");
        String email = request.getParameter("email-user");
        String pass = request.getParameter("password-user");
        String direccion = request.getParameter("direccion-user");

        Connection conn = null;
        PreparedStatement psCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement psUser = null;
        PreparedStatement psEmail = null;

        try {
            conn = ConnectionDB.gConnectionDB();

            // ── 2. VALIDACIÓN: ¿el documento ya existe en TBL_USUARIOS? ────
            String sqlCheckDoc = "SELECT DOCUMENTO_NIT FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            psCheck = conn.prepareStatement(sqlCheckDoc);
            psCheck.setString(1, nit);
            rsCheck = psCheck.executeQuery();

            if (rsCheck.next()) {
                responder(esAjax, response, "error", "documento_existente",
                        "JSPS/Login.jsp?error=documento_existente");
                return;
            }
            rsCheck.close();
            psCheck.close();

            // ── 3. VALIDACIÓN: ¿el correo ya existe en TBL_Correos? ────────
            String sqlCheckCorreo = "SELECT Correo FROM TBL_Correos WHERE Correo = ?";
            psCheck = conn.prepareStatement(sqlCheckCorreo);
            psCheck.setString(1, email);
            rsCheck = psCheck.executeQuery();

            if (rsCheck.next()) {
                responder(esAjax, response, "error", "correo_existente",
                        "JSPS/Login.jsp?error=correo_existente");
                return;
            }
            rsCheck.close();
            psCheck.close();

            // ── 4. INSERCIÓN en transacción ─────────────────────────────────
            conn.setAutoCommit(false);

            String sqlUser = "INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (?, ?, ?, ?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, nit);
            psUser.setInt(2, 3); // Rol de Usuario
            psUser.setString(3, nombre);
            psUser.setString(4, direccion);
            psUser.setString(5, SecurityUtils.hashPassword(pass));
            psUser.setInt(6, 6); // Ciudad Bucaramanga por defecto
            psUser.executeUpdate();

            String sqlEmail = "INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES (?, ?)";
            psEmail = conn.prepareStatement(sqlEmail);
            psEmail.setString(1, email);
            psEmail.setString(2, nit);
            psEmail.executeUpdate();

            conn.commit();
            responder(esAjax, response, "ok", null,
                    "JSPS/Login.jsp?registro=usuario");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            responder(esAjax, response, "error", "registro_fallido",
                    "JSPS/Login.jsp?error=registro_fallido");
        } finally {
            try {
                if (rsCheck != null)
                    rsCheck.close();
            } catch (Exception e) {
            }
            try {
                if (psCheck != null)
                    psCheck.close();
            } catch (Exception e) {
            }
            try {
                if (psUser != null)
                    psUser.close();
            } catch (Exception e) {
            }
            try {
                if (psEmail != null)
                    psEmail.close();
            } catch (Exception e) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Si la petición viene de fetch() devuelve JSON;
     * si viene de un submit tradicional, redirige.
     */
    private void responder(boolean esAjax, HttpServletResponse response,
            String status, String codigo, String urlFallback) throws IOException {
        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            if ("ok".equals(status)) {
                out.print("{\"status\":\"ok\"}");
            } else {
                out.print("{\"status\":\"error\",\"codigo\":\"" + codigo + "\"}");
            }
            out.flush();
        } else {
            response.sendRedirect(urlFallback);
        }
    }
}