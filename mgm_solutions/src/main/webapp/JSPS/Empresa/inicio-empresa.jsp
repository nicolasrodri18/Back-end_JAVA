<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS</title>
    <link rel="stylesheet" href="../../styles.css">
</head>

<body class="grid-navegacion">
    <input type="checkbox" id="toggle-modulo-cerrar-sesion" hidden>

    <div class="modulo__overlay--cerrar-sesion">
        <div class="modulo__cerrar-sesion">
            <p class="modulo__cerrar-sesion--texto">Cerraras Sesion deseas Continuar?</p>
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
                <h5 class="navbar__perfil--nombre">Hi, Cafeteria Sol</h5>
            </div>

            <div class="navbar__desplegable">
                <label for="toggle-modulo-cerrar-sesion" class="boton__cerrar-sesion">Cerrar Sesion</label>
            </div>
        </div>

        <div class="navbar__navegacion--links">
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
        <h1 class="cuerpo__contenido">
            Hola Cafeteria Sol, Bienvenid@
        </h1>
        <h1 class="cuerpo__contenido">
            Puedes dirigirte a los menus pertinentes
            <br>
            para realizar tus actividades
        </h1>
        <h1 class="cuerpo__contenido">
            Gracias por preferirnos
        </h1>
    </main>
    <footer class="pie-pagina">
        <div class="pie-pagina__contenido-1">
            <h5 class="pie-pagina__contenido-1--texto">
                Que Hacemos?
                <br>
                Llevamos el control de tu productos
                <br>
                - Almacenamiento
                <br>
                - Costes
                <br>
                - Balances
                <br>
                - Ventas
                <br>
                - Finanzas
            </h5>
        </div>
        <div class="pie-pagina__contenido-2">
            <div class="pie-pagina__contenido-2--logo">
                <img src="../../IMG/Logo_footer.png" alt="Logo.png" class="pie-pagina__logo logo-footer">
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
                <br>
                Telefono: +57 318 748 2675
                <br>
                Direccion: Calle 42 # 18 - 52 Rincon de Giron
                <br>
                Segundo Piso
                <br>
                Ubicacion: Bucaramanga / Giron
                <br>
                ©2025 - MGM Solutions
            </h5>
        </div>
    </footer>
</body>

</html>