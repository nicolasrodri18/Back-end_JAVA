package com.mgm_solutions.controllers;

import com.mgm_solutions.config.ConnectionDB;
import com.mgm_solutions.config.SecurityUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/RegistroUsuarioServlet")
public class RegistroUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recibir datos del formulario (nombres del HTML corregido)
        String nombre = request.getParameter("nombre-completo");
        String nit = request.getParameter("nit-documento");
        String email = request.getParameter("email-user");
        String pass = request.getParameter("password-user");
        String direccion = request.getParameter("direccion-user");

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psEmail = null;

        try {
            conn = ConnectionDB.gConnectionDB();
            // Desactivamos el auto-commit para que ambas inserciones sean una sola
            // "transacción"
            conn.setAutoCommit(false);

            // 2. Insertar en TBL_USUARIOS
            // Nota: ID_ROL lo pondremos como 3 (Usuario) y Ciudad como 6 (Bucaramanga por
            // defecto)
            String sqlUser = "INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (?, ?, ?, ?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, nit);
            psUser.setInt(2, 3); // Rol de Usuario
            psUser.setString(3, nombre);
            psUser.setString(4, direccion);
            psUser.setString(5, SecurityUtils.hashPassword(pass));
            psUser.setInt(6, 6); // Ciudad Bucaramanga
            psUser.executeUpdate();

            // 3. Insertar en TBL_Correos
            String sqlEmail = "INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES (?, ?)";
            psEmail = conn.prepareStatement(sqlEmail);
            psEmail.setString(1, email);
            psEmail.setString(2, nit);
            psEmail.executeUpdate();

            // Si todo salió bien, guardamos cambios
            conn.commit();
            response.sendRedirect("index.jsp?registro=creado_con_seguridad");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            response.sendRedirect("index.jsp?error=registro_fallido");
        } finally {
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
}