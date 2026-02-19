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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        String nitInput = request.getParameter("nit-documento");
        String passInput = request.getParameter("password");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDB.gConnectionDB();

            String sql = "SELECT DOCUMENTO_NIT, Nombre, Contraseña, ID_ROL FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nitInput);
            rs = ps.executeQuery();

            if (rs.next()) {
                String hashAlmacenado = rs.getString("Contraseña");

                if (SecurityUtils.checkPassword(passInput, hashAlmacenado)) {
                    // LOGIN EXITOSO: guardar sesión
                    HttpSession session = request.getSession();
                    session.setAttribute("userName", rs.getString("Nombre"));
                    int rol = rs.getInt("ID_ROL");
                    session.setAttribute("userRol", rol);

                    // Mensaje de éxito (flash en sesión) para que la JSP lo muestre si lo desea
                    session.setAttribute("successMessage", "Inicio de sesión exitoso");

                    // Determinar redirección según rol
                    String redirectUrl;
                    String jsonRedirect;
                    switch (rol) {
                        case 1: // Administrador -> seleccionar rol
                            redirectUrl = "JSPS/seleccion-rol.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/seleccion-rol.jsp";
                            break;
                        case 2: // Empresa
                            redirectUrl = "JSPS/Empresa/inicio-empresa.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/Empresa/inicio-empresa.jsp";
                            break;
                        case 3: // Usuario/Empleado
                            redirectUrl = "JSPS/Empleado/inicio-empleado.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/Empleado/inicio-empleado.jsp";
                            break;
                        default:
                            redirectUrl = "JSPS/seleccion-rol.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/seleccion-rol.jsp";
                            break;
                    }

                    responder(esAjax, response, "ok", null, redirectUrl, jsonRedirect);
                } else {
                    // Contraseña incorrecta
                    responder(esAjax, response, "error", "contrasena_incorrecta",
                            "index.jsp?loginError=1", null);
                }
            } else {
                // Usuario no encontrado
                responder(esAjax, response, "error", "usuario_no_encontrado",
                        "index.jsp?loginError=2", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responder(esAjax, response, "error", "error_servidor",
                    "index.jsp?loginError=db", null);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (ps != null)
                    ps.close();
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
     * si es un submit tradicional, redirige.
     *
     * @param redirectUrl  URL para redirect normal
     * @param jsonRedirect URL que el JS usará para navegar en caso de éxito (puede
     *                     ser null en errores)
     */
    private void responder(boolean esAjax, HttpServletResponse response,
            String status, String codigo,
            String redirectUrl, String jsonRedirect) throws IOException {

        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            if ("ok".equals(status)) {
                out.print("{\"status\":\"ok\",\"redirect\":\"" + jsonRedirect + "\"}");
            } else {
                out.print("{\"status\":\"error\",\"codigo\":\"" + codigo + "\"}");
            }
            out.flush();
        } else {
            response.sendRedirect(redirectUrl);
        }
    }
}