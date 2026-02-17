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

@WebServlet("/RegistroEmpresaServlet")
public class RegistroEmpresaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Capturar datos del formulario
        String nombreEmpresa = request.getParameter("nombre-empresa");
        String nit = request.getParameter("nit-empresa");
        String email = request.getParameter("email");
        String passRaw = request.getParameter("password"); 
        String passEncriptada = SecurityUtils.hashPassword(passRaw); 
        String ciudadId = request.getParameter("ciudad");
        String direccion = request.getParameter("direccion");

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psEmail = null;

        try {
            conn = ConnectionDB.gConnectionDB();
            conn.setAutoCommit(false); // Iniciamos transacción

            // Asegúrate de que la ñ esté escrita exactamente como en Workbench/phpMyAdmin
            String sqlUser = "INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (?, ?, ?, ?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, nit);
            psUser.setInt(2, 2); // ROL EMPRESA
            psUser.setString(3, nombreEmpresa);
            psUser.setString(4, direccion);
            psUser.setString(5, passEncriptada);
            psUser.setInt(6, Integer.parseInt(ciudadId));
            
            psUser.executeUpdate();

            // 3. Insertar en TBL_Correos
            String sqlEmail = "INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES (?, ?)";
            psEmail = conn.prepareStatement(sqlEmail);
            psEmail.setString(1, email);
            psEmail.setString(2, nit);
            
            psEmail.executeUpdate();

            // 4. Confirmar cambios
            conn.commit();
            response.sendRedirect("index.jsp?registro=success");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("index.jsp?error=empresa_existente");
        } finally {
            try { if (psUser != null) psUser.close(); } catch (Exception e) {}
            try { if (psEmail != null) psEmail.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}