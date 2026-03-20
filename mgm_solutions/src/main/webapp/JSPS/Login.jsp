<%@ page import="com.mgm_solutions.dao.CiudadDAO" %>
<%@ page import="com.mgm_solutions.models.Ciudad" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- 
    Login.jsp 
    Punto de entrada principal para la autenticación y registro de nuevos usuarios.
    Utiliza un sistema de pestañas (toggles) para alternar entre Inicio de Sesión,
    Registro de Usuario y Registro de Empresa.
--%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS</title>
    <script src="${pageContext.request.contextPath}/JS/custom_alert.js" defer></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body class="grid-inicio">
    <header class="encabezado">
        <img src="${pageContext.request.contextPath}/IMG/Logo.png" alt="Logo" class="encabezado__logo logo">
    </header>

    <main class="cuerpo">
        <input type="checkbox" id="toggle-registro__empresa" hidden>
        <input type="checkbox" id="toggle-registro__usuario" hidden>

        <section class="cuerpo__menu-inicio">
            <form action="${pageContext.request.contextPath}/LoginServlet" method="POST" class="menu-inicio--formulario">
                <div class="menu-campo">
                    <label for="nit-documento">NIT - Documento</label>
                    <input type="text" id="nit-documento" name="nit-documento" placeholder="NIT - Documento" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="password">Contraseña</label>
                    <input type="password" id="password" name="password" placeholder="Contrasena" class="campo_form" required>
                </div>

                <div class="menu-inicio--enlaces">
                    <button type="submit" class="menu-inicio--enlaces--boton boton">Iniciar Sesion</button>
                </div>
            </form>
            
            <div class="menu-inicio--enlaces">
                <p class="menu-inicio--enlaces--menus">
                    <label for="toggle-registro__empresa" class="menu-inicio__enlaces--menus--registro">Registrar Empresa</label> 
                    / 
                    <label for="toggle-registro__usuario" class="menu-inicio__enlaces--menus--registro">Registrar Usuario</label>
                </p>
            </div>
        </section>

        <section class="cuerpo__menu-registro-empresa">
            <form action="${pageContext.request.contextPath}/RegistroEmpresaServlet" method="POST" class="menu-registro-empresa--formulario">
                <div class="menu-campo">
                    <label for="nombre-empresa">Nombre de la Empresa</label>
                    <input type="text" id="nombre-empresa" name="nombre-empresa" placeholder="Nombre de la Empresa" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="nit-empresa">NIT de la Empresa</label>
                    <input type="text" id="nit-empresa" name="nit-empresa"
                           placeholder="Ej: 900123456-7"
                           class="campo_form" required
                           minlength="11" maxlength="13"
                           pattern="[0-9\-]{11,13}"
                           title="NIT: entre 11 y 13 caracteres, solo números y guión (-)">
                </div>

                <div class="menu-campo">
                    <label for="email">Correo Electrónico</label>
                    <input type="email" id="email" name="email" placeholder="Correo Electrónico" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="password-empresa">Contraseña</label>
                    <input type="password" id="password-empresa" name="password" placeholder="Contraseña" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="ciudad">Ciudad</label>
                    <select id="ciudad" name="ciudad" class="campo_form" required>
                        <option value="" disabled selected>Seleccione una ciudad</option>
                        <%
                            try {
                                CiudadDAO dao = new CiudadDAO();
                                List<Ciudad> ciudades = dao.listar();
                                if (ciudades != null && !ciudades.isEmpty()) {
                                    for (Ciudad c : ciudades) {
                        %>
                            <option value="<%= c.getId() %>"><%= c.getNombre() %></option>
                        <%
                                    }
                                } else {
                        %>
                            <option value="">No hay ciudades cargadas</option>
                        <%
                                }
                            } catch (Exception e) {
                        %>
                            <option value="">Error al cargar ciudades</option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div class="menu-campo">
                    <label for="direccion">Dirección</label>
                    <input type="text" id="direccion" name="direccion" placeholder="Direccion" class="campo_form">
                </div>

                <div class="menu-inicio--enlaces">
                    <button type="submit" class="menu-inicio--enlaces--boton boton">Registrarse</button>
                </div>
            </form>

            <div class="menu-inicio--enlaces">
                <p class="menu-inicio--enlaces--menus">
                    <label for="toggle-registro__empresa" class="menu-inicio__enlaces--menus--registro">Volver al Inicio</label>
                </p>
            </div>
        </section>

        <section class="cuerpo__menu-registro-usuario">
            <form action="${pageContext.request.contextPath}/RegistroUsuarioServlet" method="POST" class="menu-registro-usuario">
                <div class="menu-campo">
                    <label for="nombre-completo">Nombre Completo</label>
                    <input type="text" id="nombre-completo" name="nombre-completo" placeholder="Nombre Completo" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="doc-usuario">N° de Documento</label>
                    <input type="text" id="doc-usuario" name="nit-documento"
                           placeholder="Ej: 12345678"
                           class="campo_form" required
                           minlength="8" maxlength="10"
                           pattern="[0-9]{8,10}"
                           inputmode="numeric"
                           title="Documento: entre 8 y 10 dígitos, solo números">
                </div>

                <div class="menu-campo">
                    <label for="email-user">Correo Electronico</label>
                    <input type="email" id="email-user" name="email-user" placeholder="Correo Electronico" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="password-user">Contraseña</label>
                    <input type="password" id="password-user" name="password-user" placeholder="Contraseña" class="campo_form" required>
                </div>

                <div class="menu-campo">
                    <label for="direccion-user">Direccion</label>
                    <input type="text" id="direccion-user" name="direccion-user" placeholder="Direccion" class="campo_form">
                </div>

                <div class="menu-inicio--enlaces">
                    <button type="submit" class="menu-inicio--enlaces--boton boton">Registrarse</button>
                </div>
            </form>

            <div class="menu-inicio--enlaces">
                <p class="menu-inicio--enlaces--menus">
                    <label for="toggle-registro__usuario" class="menu-inicio__enlaces--menus--registro">Volver al Inicio</label>
                </p>
            </div>
        </section>
    </main>

    <footer class="pie-pagina">
        <div class="pie-pagina__contenido-1">
            <h5 class="pie-pagina__contenido-1--texto">
                Que Hacemos?
                <br>Llevamos el control de tu productos
                <br>- Almacenamiento
                <br>- Costes
                <br>- Balances
                <br>- Ventas 
                <br>- Finanzas
            </h5>
        </div>
        <div class="pie-pagina__contenido-2">
            <div class="pie-pagina__contenido-2--logo">
                <img src="${pageContext.request.contextPath}/IMG/Logo_footer.png" alt="Logo" class="pie-pagina__logo logo-footer">
            </div>
            <div class="pie-pagina__contenido-2--instagram">
                <img src="${pageContext.request.contextPath}/IMG/Logo_instagram.png" alt="Instagram" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">@MGM_SOLUTIONS</h5>
            </div>
            <div class="pie-pagina__contenido-2--facebook">
                <img src="${pageContext.request.contextPath}/IMG/Logo_facebook.png" alt="Facebook" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">@MGM_SOLUTIONS</h5>
            </div>
            <div class="pie-pagina__contenido-2--email">
                <img src="${pageContext.request.contextPath}/IMG/Logo_email.png" alt="Email" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">MGM_SOLUTIONS@gmail.com</h5>
            </div>
        </div>
        <div class="pie-pagina__contenido-3">
            <h5 class="pie-pagina__contenido-1--texto">
                Contactanos:
                <br>Telefono: +57 318 748 2675
                <br>Direccion: Calle 42 # 18 - 52 Rincon de Giron
                <br>Segundo Piso
                <br>Ubicacion: Bucaramanga / Giron
                <br>©2025 - MGM Solutions
            </h5>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <script src="../Script.js"></script>
</body>
</html>