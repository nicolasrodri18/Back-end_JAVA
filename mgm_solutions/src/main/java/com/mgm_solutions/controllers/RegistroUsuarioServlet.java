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

        // 1. Recepción y captura de los parámetros enviados desde el formulario de registro de usuario.
        String nombre = request.getParameter("nombre-completo"); // Nombre y apellido del usuario.
        String nit = request.getParameter("nit-documento"); // Documento de identidad (Cédula/NIT).
        String email = request.getParameter("email-user"); // Correo personal para notificaciones.
        String pass = request.getParameter("password-user"); // Contraseña sin procesar.
        String direccion = request.getParameter("direccion-user"); // Dirección de residencia.

        Connection conn = null;
        PreparedStatement psCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement psUser = null;
        PreparedStatement psEmail = null;

        try {
            conn = ConnectionDB.gConnectionDB();

            // ── 2. VALIDACIÓN TÉCNICA: ¿El número de documento ya está en la base de datos? ────
            String sqlCheckDoc = "SELECT DOCUMENTO_NIT FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            psCheck = conn.prepareStatement(sqlCheckDoc);
            psCheck.setString(1, nit); // Se enlaza el documento capturado.
            rsCheck = psCheck.executeQuery(); // Se ejecuta la consulta.

            if (rsCheck.next()) {
                // Si hay coincidencia, se aborta y se informa al usuario.
                responder(esAjax, response, "error", "documento_existente",
                        "JSPS/Login.jsp?error=documento_existente");
                return;
            }
            rsCheck.close(); // Cierre del conjunto de resultados intermedio.
            psCheck.close(); // Cierre de la sentencia intermedia.

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

            // ── 4. PERSISTENCIA DE DATOS CON CONTROL TRANSACCIONAL ─────────────────────────
            // Desactiva el auto-commit para garantizar que el usuario y su correo se creen atómicamente.
            conn.setAutoCommit(false);

            // Inserción en la tabla de usuarios asignando el rol 'Usuario' (3).
            String sqlUser = "INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (?, ?, ?, ?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, nit);
            psUser.setInt(2, 3); // ID 3 representa el rol estándar de un trabajador o cliente.
            psUser.setString(3, nombre);
            psUser.setString(4, direccion);
            // Se encripta la contraseña antes de guardarla para cumplir con estándares de seguridad.
            psUser.setString(5, SecurityUtils.hashPassword(pass));
            psUser.setInt(6, 6); // Se asigna '6' (Bucaramanga) como ciudad predeterminada para nuevos registros.
            psUser.executeUpdate();

            // Vinculación del correo electrónico con el documento del usuario recién creado.
            String sqlEmail = "INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES (?, ?)";
            psEmail = conn.prepareStatement(sqlEmail);
            psEmail.setString(1, email);
            psEmail.setString(2, nit);
            psEmail.executeUpdate();

            // Si los dos registros se completaron correctamente, se confirman los cambios.
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