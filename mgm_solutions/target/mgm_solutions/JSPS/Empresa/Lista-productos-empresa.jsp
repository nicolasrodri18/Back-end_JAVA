<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS - Gestión de Productos</title>
    <link rel="stylesheet" href="../../styles.css">
    <script src="../../JS/perfil-empresa.js" defer></script>
</head>

<body class="grid-navegacion">
    <input type="checkbox" id="toggle-modulo-agregar" hidden>
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

    <!-- Modal Agregar Producto -->
    <div class="modulo__overlay" id="modal-agregar">
        <div class="modulo__agregar--producto">
            <form class="modulo__formulario" id="form-agregar-producto">
                <h2 class="modulo__titulo" style="color: var(--color_primario); margin-bottom: 20px;">Nuevo Producto
                </h2>
                <div class="modulo__campo">
                    <label for="nombre-producto">Nombre del Producto:</label>
                    <input type="text" id="nombre-producto" name="nombre-producto" placeholder="Nombre del Producto"
                        class="modulo__input" required>
                </div>

                <div class="modulo__campo">
                    <label for="stock">Stock Inicial:</label>
                    <input type="number" id="stock" name="stock" placeholder="Stock" class="modulo__input" required>
                </div>

                <div class="modulo__campo">
                    <label for="valor-venta">Precio de Venta:</label>
                    <input type="number" id="valor-venta" name="valor-venta" placeholder="Valor de Venta"
                        class="modulo__input" required>
                </div>

                <div class="modulo__botones">
                    <label for="toggle-modulo-agregar" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
                    <button type="submit" class="modulo__boton modulo__boton--agregar"
                        style="border:none;">Guardar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Detalle/Editar Producto -->
    <div class="modulo__overlay--seleccionar" id="modal-detalle">
        <div class="modulo__seleccionar--producto">
            <div class="modulo__detalle" id="detalle-producto-empresa">
                <p class="modulo__detalle--texto"><strong>ID Producto:</strong> <span id="det-prod-id">---</span></p>
                <p class="modulo__detalle--texto"><strong>Nombre:</strong> <span id="det-prod-nombre">---</span></p>
                <p class="modulo__detalle--texto"><strong>Stock:</strong> <span id="det-prod-stock">---</span></p>
                <p class="modulo__detalle--texto"><strong>Precio:</strong> $<span id="det-prod-precio">---</span></p>
            </div>

            <div class="modulo__botones--seleccionar">
                <label for="toggle-modulo-seleccionar" class="modulo__boton modulo__boton--cancelar">Cerrar</label>
            </div>
            <div class="modulo__botones--acciones">
                <button type="button" id="btn-eliminar-producto" class="modulo__boton modulo__boton--eliminar"
                    style="border:none;">Eliminar</button>
                <button type="button" id="btn-editar-producto" class="modulo__boton modulo__boton--anadir"
                    style="border:none;">Editar</button>
            </div>
        </div>
    </div>

    <header class="navbar">
        <div class="navbar__perfil">
            <div class="navbar__perfil--usuario">
                <img src="../../IMG/icono-perfil.png" alt="Perfil" class="navbar__perfil--imagen icon-perfil">
                <h5 class="navbar__perfil--nombre" id="navbar-nombre">Hi, ...</h5>
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

    <main class="grid-cuerpo__contenidos">
        <section class="cuerpo__accionar cuerpo__accionar--conboton">
            <div class="cuerpo__busqueda">
                <form class="cuerpo__Barra__busqueda">
                    <input type="text" class="cuerpo__input--busqueda" placeholder="Buscar Producto: ID, Nombre, Etc"
                        name="search_query">
                    <button type="submit" class="cuerpo__boton--busqueda">
                        <img src="../../IMG/icon-lupa.png" alt="Buscar" class="cuerpo__icono--busqueda">
                    </button>
                </form>
            </div>
            <label for="toggle-modulo-agregar" class="cuerpo__boton--interaccion">Agregar Producto</label>
        </section>
        <section class="cuerpo__contenido">
            <div class="cuerpo__contenido__cards">
                <p style="text-align: center; width: 100%; padding: 2rem;">Cargando inventario...</p>
            </div>
        </section>
    </main>

    <footer class="pie-pagina">
        <div class="pie-pagina__contenido-1">
            <h5 class="pie-pagina__contenido-1--texto">
                Que Hacemos?
                <br> Llevamos el control de tu productos
                <br> - Almacenamiento
                <br> - Costes
                <br> - Balances
                <br> - Ventas
                <br> - Finanzas
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