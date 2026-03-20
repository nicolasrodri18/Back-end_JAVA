<%-- 
    Listas-empleados-empresa.jsp 
    Gestión de personal de la empresa. Permite buscar nuevos empleados por documento,
    enviar invitaciones y gestionar el estado (Activo/Inactivo) de la nómina.
--%>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>MGM_SOLUTIONS - Lista de Empleados</title>
  <link rel="stylesheet" href="../../styles.css">
  <script src="../../JS/custom_alert.js" defer></script>
  <script src="../../JS/perfil-empresa.js" defer></script>
</head>

<body class="grid-navegacion">
  <input type="checkbox" id="toggle-modulo-cerrar-sesion" hidden>
  <input type="checkbox" id="toggle-modulo-detalle" hidden>
  <input type="checkbox" id="toggle-modulo-agregar-empleado" hidden>
  <input type="checkbox" id="toggle-modulo-confirmar-invitacion" hidden>
  <input type="checkbox" id="toggle-modulo-eliminar" hidden>

  <div class="modulo__overlay--cerrar-sesion">
    <div class="modulo__cerrar-sesion">
      <p class="modulo__cerrar-sesion--texto">Cerraras Sesion ¿deseas continuar?</p>
      <div class="modulo__cerrar-sesion--botones">
        <label for="toggle-modulo-cerrar-sesion" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
        <a href="../Login.jsp" class="modulo__boton modulo__boton--continuar">Continuar</a>
      </div>
    </div>
  </div>

  <!-- Modales para Detalle, Agregar y Eliminar (se mantienen las estructuras para ser usadas por JS) -->
  <div class="modulo__overlay--detalle">
    <div class="modulo__detalle-registro">
      <h2 class="modulo__titulo" style="color: var(--color_primario); margin-bottom: 20px;">Detalle Empleado</h2>
      <div id="detalle-empleado-contenido">
        <!-- Se llenará dinámicamente si es necesario -->
      </div>
      <div class="modulo__detalle-registro--botones">
        <label for="toggle-modulo-detalle" class="modulo__boton--cerrar">Cerrar</label>
      </div>
    </div>
  </div>

  <div class="modulo__overlay--agregar-empleado">
    <div class="modulo__agregar-empleado">
      <div class="modulo__formulario">
        <h2 class="modulo__titulo" style="color: var(--color_primario); margin-bottom: 20px;">Agregar Empleado</h2>
        <div class="modulo__campo">
          <label for="id-empleado">Buscar por Documento (NIT/Cedula):</label>
          <div style="display: flex; gap: 10px;">
            <input type="text" id="id-empleado" name="id-empleado" placeholder="ID Empleado" class="modulo__input"
              style="flex: 1;">
            <button type="button" id="btn-buscar-empleado" class="modulo__boton modulo__boton--agregar"
              style="width: auto;">Buscar</button>
          </div>
        </div>

        <div id="resultado-busqueda"
          style="margin-top: 20px; display: none; padding: 15px; border: 1px dashed var(--color_primario); border-radius: 10px;">
          <p><strong>Nombre:</strong> <span id="res-nombre"></span></p>
          <p><strong>Correo:</strong> <span id="res-correo"></span></p>
          <input type="hidden" id="res-nit">
          <button type="button" id="btn-enviar-invitacion" class="modulo__boton modulo__boton--agregar"
            style="margin-top: 15px; width: 100%;">Agregar Directamente</button>
        </div>

        <div id="mensaje-busqueda" style="margin-top: 10px; color: red; display: none;"></div>

        <div class="modulo__botones" style="margin-top: 30px;">
          <label for="toggle-modulo-agregar-empleado" class="modulo__boton modulo__boton--cancelar">Cerrar</label>
        </div>
      </div>
    </div>
  </div>

  <div class="modulo__overlay--eliminar">
    <div class="modulo__eliminar">
      <p class="modulo__eliminar--texto">Seguro que deseas borrar <br> este empleado de tu lista?</p>
      <div class="modulo__eliminar--botones">
        <label for="toggle-modulo-eliminar" class="modulo__boton modulo__boton--cancelar">Cancelar</label>
        <label for="toggle-modulo-eliminar" class="modulo__boton modulo__boton--confirmar"
          id="btn-confirmar-eliminar">Confirmar</label>
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

  <main class="grid-cuerpo__contenidos">
    <section class="cuerpo__accionar cuerpo__accionar--conboton">
      <div class="cuerpo__busqueda">
        <form class="cuerpo__Barra__busqueda" onsubmit="return false;">
          <input type="text" class="cuerpo__input--busqueda"
            placeholder="Buscar Empleado: ID Registro, Nombre, Documento, Correo, Etc" name="search_query">
          <button type="button" class="cuerpo__boton--busqueda" id="btn-buscar-lista">
            <img src="../../IMG/icon-lupa.png" alt="Buscar" class="cuerpo__icono--busqueda">
          </button>
        </form>
      </div>
      <label for="toggle-modulo-agregar-empleado" class="cuerpo__boton--interaccion">Agregar Empleado</label>
    </section>
    <section class="cuerpo__contenido">
      <div class="cuerpo__contenido__cards grid__lista">
        <h1 class="cuerpo--texto">Lista Empleados</h1>
        <div class="cuerpo--cards">
          <!-- JS poblará esto -->
          <p style="text-align: center; width: 100%; grid-column: 1 / -1; padding: 2rem;">Cargando empleados...</p>
        </div>
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

  <!-- Script para poblar el nombre en el navbar (mismo que perfil empleado) -->
  <script>
    fetch("../../RelacionLaboralServlet?action=getPerfil")
      .then(res => res.json())
      .then(data => {
        document.getElementById('navbar-nombre').textContent = `Hi, ${data.userName}`;
      });
  </script>
</body>

</html>