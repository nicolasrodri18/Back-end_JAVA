<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS - Productos</title>
    <link rel="stylesheet" href="../../styles.css">
    <script src="../../JS/perfil-empleado.js" defer></script>
</head>

<body class="grid-navegacion">
    <input type="checkbox" id="toggle-modulo-seleccionar" hidden>
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

    <!-- Modal Seleccionar Producto -->
    <div class="modulo__overlay--seleccionar">
        <div class="modulo__seleccionar--producto">
            <div class="modulo__detalle" id="detalle-producto-vender">
                <p class="modulo__detalle--texto"><strong>ID Producto:</strong> <span id="sel-prod-id">---</span></p>
                <p class="modulo__detalle--texto"><strong>Nombre:</strong> <span id="sel-prod-nombre">---</span></p>
                <p class="modulo__detalle--texto"><strong>Stock:</strong> <span id="sel-prod-stock">---</span></p>
                <p class="modulo__detalle--texto"><strong>Valor:</strong> $<span id="sel-prod-precio">---</span></p>
            </div>

            <div class="modulo__cantidad">
                <span class="modulo__cantidad--texto">Selecciona la Cantidad a vender:</span>
                <div class="modulo__cantidad--controles">
                    <input type="number" id="vender-cantidad" value="0" min="1" class="modulo__cantidad--input">
                </div>
            </div>

            <div class="modulo__total">
                <p class="modulo__detalle--texto"><strong>Total:</strong> $<span id="sel-total">0</span></p>
            </div>

            <div class="modulo__botones--acciones">
                <label for="toggle-modulo-seleccionar" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
                <button type="button" id="btn-confirmar-venta" class="modulo__boton modulo__boton--vender"
                    style="border:none;">Vender</button>
            </div>
        </div>
    </div>

    <header class="navbar">
        <div class="navbar__perfil">
            <div class="navbar__perfil--usuario">
                <img src="../../IMG/icono-perfil.png" alt="Perfil" class="navbar__perfil--imagen icon-perfil">
                <h5 class="navbar__perfil--nombre">Hi, ...</h5>
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

    <main class="grid-cuerpo__contenidos">
        <section class="cuerpo__accionar">
            <div class="cuerpo__busqueda">
                <form class="cuerpo__Barra__busqueda">
                    <input type="text" class="cuerpo__input--busqueda"
                        placeholder="Buscar Producto: ID Producto, Nombre, Etc" name="search_query">
                    <button type="submit" class="cuerpo__boton--busqueda">
                        <img src="../../IMG/icon-lupa.png" alt="Buscar" class="cuerpo__icono--busqueda">
                    </button>
                </form>
            </div>
        </section>
        <section class="cuerpo__contenido">
            <div class="cuerpo__contenido__cards">
                <h2 style="text-align: center; width: 100%; padding: 1rem; color: var(--color_primario);">Lista de
                    productos</h2>
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
</body>

</html>