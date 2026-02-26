<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS - Perfil Empleado</title>
    <link rel="stylesheet" href="../../styles.css">
    <script src="../../JS/perfil-empleado.js" defer></script>
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
                <h5 class="navbar__perfil--nombre">Hi, Cargando...</h5>
            </div>

            <div class="navbar__desplegable">
                <label for="toggle-modulo-cerrar-sesion" class="boton__cerrar-sesion">Cerrar Sesion</label>
            </div>
        </div>

        <div class="navbar__navegacion--links">
            <a href="Datos-empleado.jsp" class="navegacion__links">Datos</a>
            <a href="Lista-productos-empleado.jsp" class="navegacion__links">Lista de Precios y Productos</a>
            <a href="inicio-empleado.jsp" class="navegacion__links">Inicio</a>
        </div>
        <div class="navbar__logo">
            <img src="../../IMG/Logo.png" alt="Logo.png" class="encabezado__logo logo__navegacion">
        </div>
    </header>

    <main class="contenido__menu--inicio">
        <div class="cuerpo__datos">
            <h1 class="datos__titulo">Cargando informacion...</h1>

            <div class="datos__empleado datos">
                <h3 class="datos__texto--titulo">Informacion Personal</h3>
                <div class="datos__campos">
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Documento:</h4>
                        <h4 id="user-doc" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Nombre:</h4>
                        <h4 id="user-name" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Correo:</h4>
                        <h4 id="user-email" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Direccion:</h4>
                        <h4 id="user-direc" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Estado:</h4>
                        <h4 id="status-relacion" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Empresa:</h4>
                        <h4 id="nombre-empresa-personal" class="campo__texto--descripcion">---</h4>
                    </div>
                </div>
            </div>

            <div class="datos__linea--divisora"></div>

            <div class="datos__empresa datos">
                <h3 class="datos__texto--titulo">Informacion Empresa</h3>
                <div class="datos__campos">
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Nit:</h4>
                        <h4 id="cia-nit" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Nombre:</h4>
                        <h4 id="cia-nombre" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Correo:</h4>
                        <h4 id="cia-email" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Direccion:</h4>
                        <h4 id="cia-direc" class="campo__texto--descripcion">---</h4>
                    </div>
                    <div class="campo__texto">
                        <h4 class="campo__texto--propiedad">Ciudad:</h4>
                        <h4 id="cia-ciudad" class="campo__texto--descripcion">---</h4>
                    </div>
                </div>
            </div>
        </div>
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
        </div>
    </footer>
</body>

</html>