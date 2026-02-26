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

            String sql = "SELECT DOCUMENTO_NIT, Nombre, Direccion, Contraseña, ID_ROL FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nitInput);
            rs = ps.executeQuery();

            if (rs.next()) {
                String hashAlmacenado = rs.getString("Contraseña");

                if (SecurityUtils.checkPassword(passInput, hashAlmacenado)) {
                    // LOGIN EXITOSO: guardar sesión
                    HttpSession session = request.getSession();
                    session.setAttribute("userDoc", nitInput);
                    session.setAttribute("userName", rs.getString("Nombre"));
                    int rol = rs.getInt("ID_ROL");
                    session.setAttribute("userRol", rol);
                    String uDir = rs.getString("Direccion");
                    session.setAttribute("userDirec", uDir != null ? uDir : "No registrada");

                    // Mensaje de éxito (flash en sesión) para que la JSP lo muestre si lo desea
                    session.setAttribute("successMessage", "Inicio de sesión exitoso");

                    // Determinar redirección según rol
                    String redirectUrl;
                    String jsonRedirect;
                    String rolNombre;
                    String nombreEmpresa = null;

                    switch (rol) {
                        case 1: // Administrador -> seleccionar rol
                            rolNombre = "Administrador";
                            redirectUrl = "JSPS/seleccion-rol.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/seleccion-rol.jsp";
                            break;
                        case 2: // Empresa
                            rolNombre = "Empresa";
                            redirectUrl = "JSPS/Empresa/inicio-empresa.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/Empresa/inicio-empresa.jsp";
                            break;
                        case 3: // Usuario/Empleado
                            rolNombre = "Usuario/Empleado";
                            redirectUrl = "JSPS/Empleado/inicio-empleado.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/Empleado/inicio-empleado.jsp";

                            // FETCH EMPLOYEE EXTRA DATA (Email)
                            String sqlUserExtra = "SELECT Correo FROM TBL_Correos WHERE DOCUMENTO_NIT = ?";
                            try (PreparedStatement psExtra = conn.prepareStatement(sqlUserExtra)) {
                                psExtra.setString(1, nitInput);
                                try (ResultSet rsExtra = psExtra.executeQuery()) {
                                    if (rsExtra.next()) {
                                        session.setAttribute("userEmail", rsExtra.getString("Correo"));
                                    } else {
                                        session.setAttribute("userEmail", "No registrado");
                                    }
                                }
                            }

                            // Fetch other user fields from rs (already has Documento, Nombre, ID_ROL) -
                            // Wait, rs doesn't have Direccion
                            // Re-fetch or add to first query? Let's add to first query for efficiency.
                            // I'll update the first query in a separate chunk to be safe.

                            // FETCH COMPANY DATA AND STATUS
                            String sqlEmpresa = "SELECT U.Nombre as CiaNombre, U.DOCUMENTO_NIT as CiaNIT, C.Nombre as CiaCiudad, U.Direccion as CiaDireccion, Corr.Correo as CiaEmail, R.Estado "
                                    + "FROM TBL_USUARIOS U "
                                    + "JOIN TBL_RELACION_LABORAL R ON U.DOCUMENTO_NIT = R.NIT_EMPRESA "
                                    + "JOIN TBL_CIUDADES C ON U.Ciudad = C.ID_Ciudad "
                                    + "LEFT JOIN TBL_Correos Corr ON U.DOCUMENTO_NIT = Corr.DOCUMENTO_NIT "
                                    + "WHERE R.NIT_EMPLEADO = ? AND R.Estado = 'Activo'";
                            try (PreparedStatement psEmp = conn.prepareStatement(sqlEmpresa)) {
                                psEmp.setString(1, nitInput);
                                try (ResultSet rsEmp = psEmp.executeQuery()) {
                                    if (rsEmp.next()) {
                                        nombreEmpresa = rsEmp.getString("CiaNombre");
                                        session.setAttribute("nombreEmpresa", nombreEmpresa);
                                        session.setAttribute("ciaNit", rsEmp.getString("CiaNIT"));
                                        session.setAttribute("ciaCiudad", rsEmp.getString("CiaCiudad"));
                                        String cDir = rsEmp.getString("CiaDireccion");
                                        session.setAttribute("ciaDirec", cDir != null ? cDir : "No registrada");
                                        String emailCia = rsEmp.getString("CiaEmail");
                                        session.setAttribute("ciaEmail", emailCia != null ? emailCia : "No registrado");
                                        session.setAttribute("statusRelacion", rsEmp.getString("Estado"));
                                    } else {
                                        session.setAttribute("nombreEmpresa", null);
                                        session.setAttribute("statusRelacion", "Disponible");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Store employee basic info
                            // userName is already stored as session.setAttribute("userName",
                            // rs.getString("Nombre"));
                            break;
                        default:
                            rolNombre = "Rol Desconocido";
                            redirectUrl = "JSPS/seleccion-rol.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/seleccion-rol.jsp";
                            break;
                    }

                    String userName = rs.getString("Nombre");
                    responder(esAjax, response, "ok", null, redirectUrl, jsonRedirect, userName, rolNombre,
                            nombreEmpresa);
                } else {
                    // Contraseña incorrecta
                    responder(esAjax, response, "error", "contrasena_incorrecta",
                            "index.jsp?loginError=1", null, null, null, null);
                }
            } else {
                // Usuario no encontrado
                responder(esAjax, response, "error", "usuario_no_encontrado",
                        "index.jsp?loginError=2", null, null, null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responder(esAjax, response, "error", "error_servidor",
                    "index.jsp?loginError=db", null, null, null, null);
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

    /*
     * Si la petición viene de fetch() devuelve JSON;
     * si es un submit tradicional, redirige.
     */
    private void responder(boolean esAjax, HttpServletResponse response,
            String status, String codigo,
            String redirectUrl, String jsonRedirect,
            String nombre, String rolNombre,
            String nombreEmpresa) throws IOException {

        if (esAjax) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            if ("ok".equals(status)) {
                out.print("{\"status\":\"ok\",\"redirect\":\"" + jsonRedirect + "\",\"nombre\":\"" + nombre
                        + "\",\"rol\":\"" + rolNombre + "\",\"nombreEmpresa\":\""
                        + (nombreEmpresa != null ? nombreEmpresa : "") + "\"}");
            } else {
                out.print("{\"status\":\"error\",\"codigo\":\"" + codigo + "\"}");
            }
            out.flush();
        } else {
            response.sendRedirect(redirectUrl);
        }
    }
}