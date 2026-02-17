package com.mgm_solutions.controllers;

import com.mgm_solutions.config.ConnectionDB;
import com.mgm_solutions.config.SecurityUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener los datos del formulario JSP
        // Usamos los nombres que pusimos en el HTML: 'nit-documento' y 'password'
        String nitInput = request.getParameter("nit-documento");
        String passInput = request.getParameter("password");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDB.gConnectionDB();
            
            // 2. Consulta SQL ajustada a tu script:
            // Buscamos en TBL_USUARIOS comparando DOCUMENTO_NIT y Contraseña
           // 1. Buscamos al usuario solo por NIT
            String sql = "SELECT DOCUMENTO_NIT, Nombre, Contraseña, ID_ROL FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nitInput);
            rs = ps.executeQuery();

            if (rs.next()) {
                String hashAlmacenado = rs.getString("Contraseña");
                
                // 2. Verificamos si la contraseña coincide
                if (SecurityUtils.checkPassword(passInput, hashAlmacenado)) {
                    // LOGIN EXITOSO
                    HttpSession session = request.getSession();
                    session.setAttribute("userName", rs.getString("Nombre"));
                    session.setAttribute("userRol", rs.getInt("ID_ROL"));
                    response.sendRedirect("JSPS/seleccion-rol.jsp");
                } else {
                    // Contraseña incorrecta
                    response.sendRedirect("index.jsp?loginError=1");
                }
            } else {
                // Usuario no encontrado
                response.sendRedirect("index.jsp?loginError=1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error de DB, avisamos al usuario
            response.sendRedirect("index.jsp?loginError=db");
        } finally {
            // Cierre de recursos para evitar fugas de memoria
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}