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
 * Servlet encargado de procesar el registro de nuevas empresas en el sistema.
 * Gestiona la validación de duplicados (NIT y correo) y la inserción transaccional
 * en las tablas de usuarios y correos.
 */
@WebServlet("/RegistroEmpresaServlet")
public class RegistroEmpresaServlet extends HttpServlet {

    /**
     * Procesa la solicitud de registro enviada por el formulario de empresas.
     * Realiza validaciones previas antes de persistir los datos.
     * 
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @throws ServletException Si ocurre un error de servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        // 1. Capturar datos enviados por el formulario de registro.
        String nombreEmpresa = request.getParameter("nombre-empresa"); // Nombre legal de la compañía.
        String nit = request.getParameter("nit-empresa"); // Identificador tributario único (NIT).
        String email = request.getParameter("email"); // Correo electrónico corporativo.
        String passRaw = request.getParameter("password"); // Contraseña en texto plano.
        // Se genera el hash seguro (BCrypt) para no almacenar la clave en texto claro.
        String passEncriptada = SecurityUtils.hashPassword(passRaw);
        String ciudadId = request.getParameter("ciudad"); // ID de la ciudad seleccionada del dropdown.
        String direccion = request.getParameter("direccion"); // Dirección física de la sede principal.

        Connection conn = null;
        PreparedStatement psCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement psUser = null;
        PreparedStatement psEmail = null;

        try {
            conn = ConnectionDB.gConnectionDB();

            // ── 2. VALIDACIÓN: Verificar si el NIT ya se encuentra registrado. ──────────
            String sqlCheckNit = "SELECT DOCUMENTO_NIT FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            psCheck = conn.prepareStatement(sqlCheckNit);
            psCheck.setString(1, nit); // Asigna el NIT a validar.
            rsCheck = psCheck.executeQuery(); // Ejecuta la búsqueda.

            if (rsCheck.next()) {
                // Si existe un registro, detiene el proceso y responde con error.
                responder(esAjax, response, "error", "nit_existente",
                        "JSPS/Login.jsp?error=nit_existente");
                return;
            }
            rsCheck.close(); // Libera el ResultSet para la siguiente validación.
            psCheck.close(); // Libera el Statement.

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

            // ── 4. INSERCIÓN MULTI-TABLA EN TRANSACCIÓN ─────────────────────────────────
            // Se desactiva el auto-commit para asegurar que el usuario y su correo se creen juntos o nada.
            conn.setAutoCommit(false);

            // Inserción en la tabla maestra de usuarios con rol de Empresa (2).
            String sqlUser = "INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (?, ?, ?, ?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, nit);
            psUser.setInt(2, 2); // ID_ROL 2 corresponde a 'Empresa'.
            psUser.setString(3, nombreEmpresa);
            psUser.setString(4, direccion);
            psUser.setString(5, passEncriptada);
            psUser.setInt(6, Integer.parseInt(ciudadId));
            psUser.executeUpdate(); // Ejecuta la inserción del usuario.

            // Inserción del correo electrónico vinculado al NIT de la empresa.
            String sqlEmail = "INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES (?, ?)";
            psEmail = conn.prepareStatement(sqlEmail);
            psEmail.setString(1, email);
            psEmail.setString(2, nit);
            psEmail.executeUpdate(); // Ejecuta la inserción del correo.

            // Si ambas operaciones fueron exitosas, se persisten los cambios permanentemente.
            conn.commit();
            responder(esAjax, response, "ok", null,
                    "JSPS/Login.jsp?registro=empresa");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            responder(esAjax, response, "error", "empresa_existente",
                    "JSPS/Login.jsp?error=empresa_existente");
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