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

/**
 * Servlet que gestiona el proceso de autenticación de usuarios.
 * Permite el acceso a Administradores, Empresas y Empleados, 
 * configurando la sesión según el rol y gestionando tanto peticiones AJAX como tradicionales.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    /**
     * Procesa la solicitud de inicio de sesión.
     * Valida las credenciales contra la base de datos y configura la sesión.
     * 
     * @param request La petición HttpServletRequest.
     * @param response La respuesta HttpServletResponse.
     * @throws ServletException Si hay errores en el servlet.
     * @throws IOException Si hay errores de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Detecta si la petición es AJAX mediante la cabecera estándar de XMLHttpRequest.
        boolean esAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        // Obtiene el NIT o documento del usuario desde el formulario.
        String nitInput = request.getParameter("nit-documento");
        // Obtiene la contraseña ingresada por el usuario.
        String passInput = request.getParameter("password");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Abre una nueva conexión a la base de datos usando el Singleton ConnectionDB.
            conn = ConnectionDB.gConnectionDB();

            // SQL para obtener los datos básicos del usuario por su documento/NIT.
            String sql = "SELECT DOCUMENTO_NIT, Nombre, Direccion, Contraseña, ID_ROL FROM TBL_USUARIOS WHERE DOCUMENTO_NIT = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nitInput); // Asigna el NIT al parámetro configurado.
            rs = ps.executeQuery(); // Ejecuta la consulta en la DB.

            // Si se encuentra un registro que coincida con el NIT...
            if (rs.next()) {
                // Obtiene el hash BCrypt almacenado en la columna 'Contraseña'.
                String hashAlmacenado = rs.getString("Contraseña");

                // Compara la contraseña en texto plano con el hash de la base de datos.
                if (SecurityUtils.checkPassword(passInput, hashAlmacenado)) {
                    // LOGIN EXITOSO: Se inicia el manejo de sesión del servidor.
                    HttpSession session = request.getSession();
                    // Registra el documento del usuario en la sesión actual.
                    session.setAttribute("userDoc", nitInput);
                    // Registra el nombre completo del usuario.
                    session.setAttribute("userName", rs.getString("Nombre"));
                    // Determina y guarda el rol (1:Admin, 2:Empresa, 3:Empleado).
                    int rol = rs.getInt("ID_ROL");
                    session.setAttribute("userRol", rol);
                    // Guarda la dirección o un valor por defecto si es nulo.
                    String uDir = rs.getString("Direccion");
                    session.setAttribute("userDirec", uDir != null ? uDir : "No registrada");

                    // Guarda un mensaje de éxito temporal en la sesión.
                    session.setAttribute("successMessage", "Inicio de sesión exitoso");

                    // Determinar redirección según rol
                    String redirectUrl;
                    String jsonRedirect;
                    String rolNombre;
                    String nombreEmpresa = null;

                    switch (rol) {
                        case 1: // Administrador: Es redirigido a la página de selección de rol.
                            rolNombre = "Administrador";
                            redirectUrl = "JSPS/seleccion-rol.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/seleccion-rol.jsp";
                            break;
                        case 2: // Empresa: Es redirigido al panel administrativo corporativo.
                            rolNombre = "Empresa";
                            redirectUrl = "JSPS/Empresa/inicio-empresa.jsp?loginSuccess=1";
                            jsonRedirect = request.getContextPath() + "/JSPS/Empresa/inicio-empresa.jsp";

                            // Obtención de datos adicionales de la empresa (Correo y Ciudad).
                            String sqlCiaData = "SELECT C.Correo, CIU.Nombre as CiudadNombre "
                                    + "FROM TBL_USUARIOS U "
                                    + "LEFT JOIN TBL_Correos C ON U.DOCUMENTO_NIT = C.DOCUMENTO_NIT "
                                    + "LEFT JOIN TBL_CIUDADES CIU ON U.Ciudad = CIU.ID_Ciudad "
                                    + "WHERE U.DOCUMENTO_NIT = ?";
                            try (PreparedStatement psCia = conn.prepareStatement(sqlCiaData)) {
                                psCia.setString(1, nitInput);
                                try (ResultSet rsCia = psCia.executeQuery()) {
                                    if (rsCia.next()) {
                                        // Guarda el correo y el nombre de la ciudad en la sesión.
                                        session.setAttribute("userEmail", rsCia.getString("Correo"));
                                        session.setAttribute("ciaCiudad", rsCia.getString("CiudadNombre"));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                                    + "WHERE R.NIT_EMPLEADO = ?";
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
            // Log de error en consola para depuración del backend.
            e.printStackTrace();
            // Envía respuesta genérica de error de servidor.
            responder(esAjax, response, "error", "error_servidor",
                    "index.jsp?loginError=db", null, null, null, null);
        } finally {
            // Bloque de cierre seguro de recursos JDBC para evitar fugas de memoria.
            try {
                if (rs != null)
                    rs.close(); // Cierra el conjunto de resultados.
            } catch (Exception e) {
            }
            try {
                if (ps != null)
                    ps.close(); // Cierra la sentencia preparada.
            } catch (Exception e) {
            }
            try {
                if (conn != null)
                    conn.close(); // Libera la conexión al pool.
            } catch (Exception e) {
            }
        }
    }

    /*
     * Si la petición viene de fetch() devuelve JSON;
     * si es un submit tradicional, redirige.
     */
    /**
     * Gestiona la respuesta al cliente dependiendo si la petición fue AJAX o no.
     * 
     * @param esAjax Verdadero si la petición es via XMLHttpRequest (fetch).
     * @param response Objeto de respuesta.
     * @param status Estado de la operación (ok/error).
     * @param codigo Código de error si aplica.
     * @param redirectUrl URL de redirección para navegación tradicional.
     * @param jsonRedirect URL de redirección para navegación vía JS.
     * @param nombre Nombre del usuario.
     * @param rolNombre Nombre del rol asignado.
     * @param nombreEmpresa Nombre de la empresa asociada (si aplica).
     * @throws IOException Si ocurre un error de escritura.
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