package com.mgm_solutions.controllers;

import com.mgm_solutions.config.ConnectionDB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Servlet de diagnóstico para verificar la comunicación entre la aplicación
 * y el servidor de base de datos MySQL en tiempo real.
 */
@WebServlet("/test-db") // Esta será la URL en el navegador
public class TestConnectionServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Intentamos la conexión desde el servidor
        Connection conn = ConnectionDB.gConnectionDB();
        
        out.println("<html><body>");
        if (conn != null) {
            out.println("<h1 style='color:green;'>✅ ¡Servidor conectado a la base de datos con éxito!</h1>");
            try { conn.close(); } catch (Exception e) {}
        } else {
            out.println("<h1 style='color:red;'>❌ Error: El servidor no pudo conectar.</h1>");
            out.println("<p>Revisa los logs de Tomcat en la consola de VS Code.</p>");
        }
        out.println("<a href='index.jsp'>Volver al inicio</a>");
        out.println("</body></html>");
    }
}
