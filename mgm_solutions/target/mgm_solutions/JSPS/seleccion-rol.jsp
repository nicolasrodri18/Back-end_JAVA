<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MGM_SOLUTIONS</title>
    <link rel="stylesheet" href="../styles.css">
</head>

<body class="grid-inicio">
    <header class="encabezado">
        <img src="../IMG/Logo.png" alt="Logo.png" class="encabezado__logo logo">
    </header>
    <main class="cuerpo">
        <div class="seleccion-rol">
            <h1 class="cuerpo__contenido--texto">Hola <%= session.getAttribute("userName") %>, Selecciona tu Rol</h1>
            <a class="seleccion-rol__empleado campo-rol" href="Empleado/inicio-empleado.jsp">
                Empleado
            </a>
            <a class="seleccion-rol__empresa campo-rol" href="Empresa/inicio-empresa.jsp">
                Empresa
            </a>
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
                <img src="../IMG/Logo_footer.png" alt="Logo.png" class="pie-pagina__logo logo-footer">
            </div>
            <div class="pie-pagina__contenido-2--instagram">
                <img src="../IMG/Logo_instagram.png" alt="Instagram" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">@MGM_SOLUTIONS</h5>
            </div>
            <div class="pie-pagina__contenido-2--facebook">
                <img src="../IMG/Logo_facebook.png" alt="Facebook" class="pie-pagina__intagram logo-redes">
                <h5 class="pie-pagina__contenido-2--texto">@MGM_SOLUTIONS</h5>
            </div>
            <div class="pie-pagina__contenido-2--email">
                <img src="../IMG/Logo_email.png" alt="Email" class="pie-pagina__intagram logo-redes">
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