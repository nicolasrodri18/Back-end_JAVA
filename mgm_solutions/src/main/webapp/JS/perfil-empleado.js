// perfil-empleado.js - MGM Solutions
document.addEventListener("DOMContentLoaded", function () {
    console.log("Perfil de Empleado cargado correctamente.");

    const fields = {
        // Personales
        'userDoc': document.getElementById('user-doc'),
        'userName': document.getElementById('user-name'),
        'userEmail': document.getElementById('user-email'),
        'userDirec': document.getElementById('user-direc'),
        'statusRelacion': document.getElementById('status-relacion'),
        'nombreEmpresa': document.getElementById('nombre-empresa-personal'),
        // Empresa
        'ciaNit': document.getElementById('cia-nit'),
        'ciaNombre': document.getElementById('cia-nombre'),
        'ciaEmail': document.getElementById('cia-email'),
        'ciaDirec': document.getElementById('cia-direc'),
        'ciaCiudad': document.getElementById('cia-ciudad'),
        // Otros
        'perfilHeader': document.querySelector('.navbar__perfil--nombre'),
        'datosTitulo': document.querySelector('.datos__titulo'),
        'contenedorEmpresa': document.querySelector('.datos__empresa'),
        'welcomeName': document.getElementById('welcome-name'),
        'companyStatus': document.getElementById('company-status')
    };

    function cargarPerfil() {
        fetch("../../RelacionLaboralServlet?action=getPerfil")
            .then(res => res.json())
            .then(data => {
                if (fields.perfilHeader) fields.perfilHeader.textContent = `Hi, ${data.userName}`;
                if (fields.welcomeName) fields.welcomeName.textContent = data.userName;
                if (fields.companyStatus) {
                    fields.companyStatus.innerHTML = data.nombreEmpresa
                        ? `Haces Parte de la empresa <br> ${data.nombreEmpresa}`
                        : "No cuentas con empresa";
                }

                if (fields.datosTitulo) {
                    fields.datosTitulo.textContent = data.nombreEmpresa
                        ? "Informacion y Contacto de tu empresa"
                        : "Estado de Vinculacion Laboral";
                }

                // Intentar cargar productos siempre, pero la función manejará si no hay empresa
                cargarProductos(data.nombreEmpresa);

                if (fields.userDoc) fields.userDoc.textContent = data.userDoc;
                if (fields.userName) fields.userName.textContent = data.userName;
                if (fields.userEmail) fields.userEmail.textContent = data.userEmail || "No registrado";
                if (fields.userDirec) fields.userDirec.textContent = data.userDirec || "No registrada";
                if (fields.statusRelacion) fields.statusRelacion.textContent = data.statusRelacion;
                if (fields.nombreEmpresa) fields.nombreEmpresa.textContent = data.nombreEmpresa || "Sin Vincular";

                if (data.nombreEmpresa && fields.contenedorEmpresa) {
                    fields.contenedorEmpresa.style.display = "block";
                    if (fields.ciaNit) fields.ciaNit.textContent = data.ciaNit;
                    if (fields.ciaNombre) fields.ciaNombre.textContent = data.nombreEmpresa;
                    if (fields.ciaEmail) fields.ciaEmail.textContent = data.ciaEmail || "No registrado";
                    if (fields.ciaDirec) fields.ciaDirec.textContent = data.ciaDirec || "No registrada";
                    if (fields.ciaCiudad) fields.ciaCiudad.textContent = data.ciaCiudad || "No registrada";
                } else if (fields.contenedorEmpresa) {
                    fields.contenedorEmpresa.innerHTML = `
                        <h3 class="datos__texto--titulo">Aviso</h3>
                        <div class="datos__campos" style="margin-right: 23%; text-align: center;">
                            <p class="campo__texto--descripcion" style="font-weight: bold; color: var(--color_primario);">
                                No cuentas con empresa, acercate a tu empresa para que te agregue.
                            </p>
                        </div>
                    `;
                }
            })
            .catch(err => console.error("Error al cargar perfil:", err));
    }

    function cargarProductos(empresaNombre) {
        const contenedor = document.querySelector('.cuerpo__contenido__cards');
        if (!contenedor) return;

        if (!empresaNombre) {
            contenedor.innerHTML = '<p style="text-align: center; width: 100%; padding: 2rem; font-weight: bold; color: var(--color_primario);">No tienes una empresa vinculada para ver la lista de productos.</p>';
            return;
        }

        fetch("../../InventarioServlet?action=listar")
            .then(res => res.json())
            .then(data => {
                if (!data || data.length === 0) {
                    contenedor.innerHTML = '<p style="text-align: center; width: 100%; padding: 2rem;">No hay productos disponibles actualmente en tu empresa.</p>';
                    return;
                }
                contenedor.innerHTML = '';
                data.forEach(prod => {
                    const card = document.createElement('div');
                    card.className = 'card__producto';
                    card.innerHTML = `
                        <div class="card__producto--texto">
                            <h3 class="card__producto--texto__contenido">ID Producto: ${prod.id}</h3>
                            <h3 class="card__producto--texto__contenido">Nombre: ${prod.nombre}</h3>
                            <h3 class="card__producto--texto__contenido">Stock: ${prod.stock}</h3>
                            <h3 class="card__producto--texto__contenido">Valor: $${prod.precio}</h3>
                        </div>
                        <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar" onclick="prepararVenta('${prod.id}', '${prod.nombre}', ${prod.stock}, ${prod.precio})">Seleccionar</label>
                    `;
                    contenedor.appendChild(card);
                });
            })
            .catch(err => {
                console.error("Error al cargar productos:", err);
                contenedor.innerHTML = '<p style="text-align: center; color: red;">Error al cargar la lista de productos.</p>';
            });
    }

    window.prepararVenta = function (id, nombre, stock, precio) {
        document.getElementById('sel-prod-id').textContent = id;
        document.getElementById('sel-prod-nombre').textContent = nombre;
        document.getElementById('sel-prod-stock').textContent = stock;
        document.getElementById('sel-prod-precio').textContent = precio;

        const inputCant = document.getElementById('vender-cantidad');
        const spanTotal = document.getElementById('sel-total');

        inputCant.value = 1;
        spanTotal.textContent = precio;

        inputCant.oninput = function () {
            const cant = parseInt(this.value) || 0;
            if (cant > stock) {
                alert("La cantidad no puede superar el stock disponible.");
                this.value = stock;
                spanTotal.textContent = (stock * precio).toFixed(2);
            } else {
                spanTotal.textContent = (cant * precio).toFixed(2);
            }
        };

        const btnConfirmar = document.getElementById('btn-confirmar-venta');
        btnConfirmar.onclick = function () {
            const cantidad = parseInt(inputCant.value);
            if (cantidad <= 0) {
                alert("Selecciona una cantidad válida.");
                return;
            }

            const formData = new URLSearchParams();
            formData.append('idRegistroAlmacen', id);
            formData.append('cantidad', cantidad);
            formData.append('total', spanTotal.textContent);

            fetch("../../VentasServlet?action=registrarVenta", {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
                .then(res => res.json())
                .then(data => {
                    alert(data.message);
                    if (data.status === "success") location.reload();
                })
                .catch(err => alert("Error al registrar la venta."));
        };
    };

    cargarPerfil();
});
