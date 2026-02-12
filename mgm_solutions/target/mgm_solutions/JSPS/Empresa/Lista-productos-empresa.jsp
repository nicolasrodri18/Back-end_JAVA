<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS</title>
    <link rel="stylesheet" href="../../styles.css">
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

    <div class="modulo__overlay">
        <div class="modulo__agregar--producto">
            <form class="modulo__formulario">
                <div class="modulo__campo">
                    <label for="id-producto">ID Producto:</label>
                    <input type="text" id="id-producto" name="id-producto" placeholder="ID Producto"
                        class="modulo__input">
                </div>

                <div class="modulo__campo">
                    <label for="nombre-producto">Nombre del Producto:</label>
                    <input type="text" id="nombre-producto" name="nombre-producto" placeholder="Nombre del Producto"
                        class="modulo__input">
                </div>

                <div class="modulo__campo">
                    <label for="stock">Stock:</label>
                    <input type="number" id="stock" name="stock" placeholder="Stock" class="modulo__input">
                </div>

                <div class="modulo__campo">
                    <label for="valor-venta">Valor de Venta:</label>
                    <input type="text" id="valor-venta" name="valor-venta" placeholder="Valor de Venta"
                        class="modulo__input">
                </div>

                <div class="modulo__campo">
                    <label for="porcentaje-ganancia">Porcentaje de Ganancia:</label>
                    <input type="text" id="porcentaje-ganancia" name="porcentaje-ganancia"
                        placeholder="Porcentaje de Ganancia" class="modulo__input">
                </div>

                <div class="modulo__campo">
                    <label for="precio-compra">Precio de Compra:</label>
                    <input type="text" id="precio-compra" name="precio-compra" placeholder="Precio de Compra"
                        class="modulo__input">
                </div>

                <div class="modulo__botones">
                    <label for="toggle-modulo-agregar" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
                    <label for="toggle-modulo-agregar" class="modulo__boton modulo__boton--agregar">Agregar</label>
                </div>
            </form>
        </div>
    </div>

    <div class="modulo__overlay--seleccionar">
        <div class="modulo__seleccionar--producto">
            <div class="modulo__detalle">
                <p class="modulo__detalle--texto"><strong>ID Producto:</strong> P-45678901234</p>
                <p class="modulo__detalle--texto"><strong>Nombre:</strong> Laptop Ejecutiva X10</p>
                <p class="modulo__detalle--texto"><strong>Stock:</strong> 50</p>
                <p class="modulo__detalle--texto"><strong>Valor:</strong> $2.500</p>
                <p class="modulo__detalle--texto"><strong>Porcentaje de Ganancia:</strong> 25%</p>
                <p class="modulo__detalle--texto"><strong>Precio de Compra:</strong> $1.875</p>
            </div>

            <div class="modulo__cantidad">
                <span class="modulo__cantidad--texto">Selecciona una cantidad:</span>
                <div class="modulo__cantidad--controles">
                    <button type="button" class="modulo__cantidad--btn">-</button>
                    <input type="number" value="0" min="0" class="modulo__cantidad--input">
                    <button type="button" class="modulo__cantidad--btn">+</button>
                </div>
            </div>

            <div class="modulo__botones--seleccionar">
                <label for="toggle-modulo-seleccionar" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
            </div>
            <div class="modulo__botones--acciones">
                <label for="toggle-modulo-seleccionar" class="modulo__boton modulo__boton--eliminar">Eliminar</label>
                <label for="toggle-modulo-seleccionar" class="modulo__boton modulo__boton--anadir">Añadir</label>
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
    <main class="grid-cuerpo__contenidos">
        <section class="cuerpo__accionar cuerpo__accionar--conboton">
            <div class="cuerpo__busqueda">
                <form class="cuerpo__Barra__busqueda">
                    <input type="text" class="cuerpo__input--busqueda"
                        placeholder="Buscar Producto: ID Producto, Nombre, Etc" name="search_query">
                    <button type="submit" class="cuerpo__boton--busqueda">
                        <img src="../../IMG/icon-lupa.png" alt="Buscar" class="cuerpo__icono--busqueda">
                    </button>
                </form>
            </div>
            <label for="toggle-modulo-agregar" class="cuerpo__boton--interaccion">
                Agregar
            </label>
        </section>
        <section class="cuerpo__contenido">
            <div class="cuerpo__contenido__cards">
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>
                <div class="card__producto">
                    <div class="card__producto--texto">
                        <h3 class="card__producto--texto__contenido">ID Producto: P-45678901234</h3>
                        <h3 class="card__producto--texto__contenido">Nombre: Laptop Ejecutiva X10</h3>
                        <h3 class="card__producto--texto__contenido">Stock: 50</h3>
                        <h3 class="card__producto--texto__contenido">Valor: $2.500</h3>
                    </div>
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar">Seleccionar</label>
                </div>  
            </div>
        </section>
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