<%-- 
    Datos-empresa.jsp 
    Vista del perfil corporativo. Permite a la empresa actualizar sus datos
    de contacto, ubicación (Ciudad) y contraseña.
--%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS - Datos Empresa</title>
    <link rel="stylesheet" href="../../styles.css">
    <script src="../../JS/custom_alert.js" defer></script>
    <script src="../../JS/datos-empresa.js" defer></script>
</head>

<body class="grid-navegacion">
    <input type="checkbox" id="toggle-modulo-cerrar-sesion" hidden>

    <div class="modulo__overlay--cerrar-sesion">
        <div class="modulo__cerrar-sesion">
            <p class="modulo__cerrar-sesion--texto">Cerraras Sesion ¿deseas continuar?</p>
            <div class="modulo__cerrar-sesion--botones">
                <label for="toggle-modulo-cerrar-sesion" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
                <a href="../Login.jsp" class="modulo__boton modulo__boton--continuar">Continuar</a>
            </div>
        </div>
    </div>

    <header class="navbar">
        <div class="navbar__perfil">
            <div class="navbar__perfil--usuario">
                <img src="../../IMG/icono-perfil.png" alt="Perfil" class="navbar__perfil--imagen icon-perfil">
                <h5 class="navbar__perfil--nombre" id="navbar-nombre">Hi, Cargando...</h5>
            </div>

            <div class="navbar__desplegable">
                <label for="toggle-modulo-cerrar-sesion" class="boton__cerrar-sesion">Cerrar Sesion</label>
            </div>
        </div>

        <div class="navbar__navegacion--links">
            <a href="Datos-empresa.jsp" class="navegacion__links">Datos</a>
            <a href="Listas-empleados-empresa.jsp" class="navegacion__links">Empleados</a>
            <a href="Listas-ventas-empresa.jsp" class="navegacion__links">Ventas</a>
            <a href="Lista-productos-empresa.jsp" class="navegacion__links">Lista de Precios y Productos</a>
            <a href="inicio-empresa.jsp" class="navegacion__links">Inicio</a>
        </div>
        <div class="navbar__logo">
            <img src="../../IMG/Logo.png" alt="Logo.png" class="encabezado__logo logo__navegacion">
        </div>
    </header>

    <main class="contenido__menu--inicio">
        <div class="cuerpo__datos">
            <h1 class="datos__titulo">Informacion de tu empresa</h1>

            <div class="datos__empleado datos" style="grid-column: 1 / -1; max-width: 600px; margin: 0 auto;">
                <h3 class="datos__texto--titulo">Datos de Registro</h3>
                <div class="datos__campos" style="margin-left: 0; align-items: center;">
                    <div class="campo__texto" style="width: 100%; justify-content: flex-start; padding-left: 20%;">
                        <h4 class="campo__texto--propiedad">Nit:</h4>
                        <h4 id="cia-nit" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto" style="width: 100%; justify-content: flex-start; padding-left: 20%;">
                        <h4 class="campo__texto--propiedad">Nombre:</h4>
                        <h4 id="cia-nombre" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto" style="width: 100%; justify-content: flex-start; padding-left: 20%;">
                        <h4 class="campo__texto--propiedad">Correo:</h4>
                        <h4 id="cia-email" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto" style="width: 100%; justify-content: flex-start; padding-left: 20%;">
                        <h4 class="campo__texto--propiedad">Direccion:</h4>
                        <h4 id="cia-direc" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto" style="width: 100%; justify-content: flex-start; padding-left: 20%;">
                        <h4 class="campo__texto--propiedad">Ciudad:</h4>
                        <h4 id="cia-ciudad" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="datos__contenedor-boton"
                        style="width: 100%; display: flex; justify-content: center; margin-top: 20px;">
                        <label id="btn-abrir-editar-perfil"
                            class="modulo__boton modulo__boton--anadir modulo__boton--editar-perfil">Editar
                            Datos</label>
                    </div>
                </div>
            </div>
        </div>
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
                <img src="../../IMG/Logo_footer.png" alt="Logo" class="pie-pagina__logo logo-footer">
            </div>
            <div class="pie-pagina__contenido-2--instagram">
                <img src="../../IMG/Logo_instagram.png" alt="Instagram" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">@MGM_SOLUTIONS</h5>
            </div>
            <div class="pie-pagina__contenido-2--facebook">
                <img src="../../IMG/Logo_facebook.png" alt="Facebook" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">@MGM_SOLUTIONS</h5>
            </div>
            <div class="pie-pagina__contenido-2--email">
                <img src="../../IMG/Logo_email.png" alt="Email" class="pie-pagina__intagram logo-redes">
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

    <input type="checkbox" id="toggle-modulo-editar-perfil" hidden>

    <!-- Modal Editar Perfil Empresa -->
    <div class="modulo__overlay" id="modal-editar-perfil">
        <div class="modulo__agregar--producto">
            <form class="modulo__formulario" id="form-editar-perfil">
                <h2 class="modulo__titulo">Editar Datos de Empresa</h2>
                <div class="modulo__campo">
                    <label for="edit-cia-nombre">Nombre de la Empresa:</label>
                    <input type="text" id="edit-cia-nombre" name="nombre" class="modulo__input" required>
                </div>
                <div class="modulo__campo">
                    <label for="edit-cia-email">Correo Electronico:</label>
                    <input type="email" id="edit-cia-email" name="email" class="modulo__input" required>
                </div>
                <div class="modulo__campo">
                    <label for="edit-cia-direc">Direccion:</label>
                    <input type="text" id="edit-cia-direc" name="direccion" class="modulo__input">
                </div>
                <div class="modulo__campo">
                    <label for="edit-cia-ciudad">Ciudad:</label>
                    <select id="edit-cia-ciudad" name="ciudad" class="modulo__input" required>
                        <!-- Se llenará con JS -->
                    </select>
                </div>
                <div class="modulo__campo">
                    <label for="edit-cia-pass">Nueva Contrasena (dejar en blanco para no cambiar):</label>
                    <input type="password" id="edit-cia-pass" name="pass" class="modulo__input">
                </div>
                <div class="modulo__botones">
                    <label for="toggle-modulo-editar-perfil"
                        class="modulo__boton modulo__boton--cancelar">Cancelar</label>
                    <button type="submit" class="modulo__boton modulo__boton--agregar" style="border: none;">Guardar
                        Cambios</button>
                </div>
            </form>
        </div>
    </div>
</body>

</html>