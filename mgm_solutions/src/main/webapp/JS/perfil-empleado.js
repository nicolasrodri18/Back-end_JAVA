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

    let todosLosProductos = [];

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
                todosLosProductos = data;
                renderProductos(todosLosProductos, contenedor);
                inicializarBusquedaProductos(contenedor);
            })
            .catch(err => {
                console.error("Error al cargar productos:", err);
                contenedor.innerHTML = '<p style="text-align: center; color: red;">Error al cargar la lista de productos.</p>';
            });
    }

    function renderProductos(lista, contenedor) {
        if (!contenedor) return;
        contenedor.innerHTML = '';
        if (lista.length === 0) {
            contenedor.innerHTML = '<p style="text-align: center; width: 100%; padding: 2rem;">No se encontraron productos.</p>';
            return;
        }

        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        lista.forEach(prod => {
            const card = document.createElement('div');
            card.className = 'card__producto';
            card.style.minWidth = '220px';
            card.style.padding = '1.2rem';

            const isOutOfStock = prod.stock <= 0;
            let vencimientoTexto = "";
            let vencimientoColor = "inherit";
            let isVencido = false;

            if (prod.vencimiento && prod.vencimiento !== "null") {
                const fechaVenc = new Date(prod.vencimiento + "T00:00:00");
                fechaVenc.setHours(0, 0, 0, 0);
                if (fechaVenc < hoy) {
                    isVencido = true;
                    vencimientoTexto = `Vencido desde: ${prod.vencimiento}`;
                    vencimientoColor = "#ff4d4d";
                } else if (fechaVenc.getTime() === hoy.getTime()) {
                    vencimientoTexto = "¡Vence Hoy!";
                    vencimientoColor = "#ffd700";
                } else {
                    vencimientoTexto = `Vence: ${prod.vencimiento}`;
                }
            }

            let accionHTML = "";
            if (isVencido) {
                accionHTML = `<span class="card__producto--boton-agotado" style="background-color: #721c24; cursor: not-allowed; padding: 0.4rem 0.8rem; border-radius: 5px; color: #f8d7da; text-align: center; font-weight: bold; font-size: 0.85rem;">VENCIDO</span>`;
            } else if (isOutOfStock) {
                accionHTML = `<span class="card__producto--boton-agotado" style="background-color: #555; cursor: not-allowed; padding: 0.5rem 1rem; border-radius: 5px; color: #aaa; text-align: center;">VACÍO</span>`;
            } else {
                accionHTML = `<label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar" onclick="prepararVenta('${prod.idRegistro}', '${prod.idProducto}', '${prod.nombre}', ${prod.stock}, ${prod.precio})">Seleccionar</label>`;
            }

            card.innerHTML = `
                <div class="card__producto--texto">
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.9rem;">ID Producto: ${prod.idProducto}</h3>
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.95rem; font-weight: bold; margin-bottom: 3px;">${prod.nombre}</h3>
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.9rem; ${isOutOfStock ? 'color: #ff4d4d; font-weight: bold;' : ''}">
                        Stock: ${isOutOfStock ? 'VACÍO' : prod.stock}
                    </h3>
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.9rem;">Valor: $${prod.precio}</h3>
                    ${vencimientoTexto ? `<h4 class="card__producto--texto__contenido" style="color: ${vencimientoColor}; font-weight: bold; font-size: 0.8rem; margin-top: 3px; line-height: 1.1;">${vencimientoTexto}</h4>` : ''}
                </div>
                <div style="margin-top: 10px; display: flex; justify-content: center;">
                    ${accionHTML}
                </div>
            `;
            contenedor.appendChild(card);
        });
    }

    function inicializarBusquedaProductos(contenedor) {
        const formBusqueda = document.querySelector('.cuerpo__Barra__busqueda');
        const inputBusqueda = document.querySelector('.cuerpo__input--busqueda');
        const btnBuscar = document.querySelector('.cuerpo__boton--busqueda');
        if (!inputBusqueda) return;

        function filtrar() {
            const query = inputBusqueda.value.toLowerCase().trim();
            if (query === '') {
                renderProductos(todosLosProductos, contenedor);
                return;
            }
            const filtrados = todosLosProductos.filter(prod =>
                String(prod.idProducto).toLowerCase().includes(query) ||
                String(prod.nombre || '').toLowerCase().includes(query) ||
                String(prod.precio || '').toLowerCase().includes(query)
            );
            renderProductos(filtrados, contenedor);
        }

        if (formBusqueda) {
            formBusqueda.addEventListener('submit', (e) => {
                e.preventDefault();
                filtr();
            });
        }
        inputBusqueda.addEventListener('input', filtrar);
        if (btnBuscar) btnBuscar.addEventListener('click', filtrar);
    }

    window.prepararVenta = function (idRegistro, idProducto, nombre, stock, precio) {
        document.getElementById('sel-prod-id').textContent = idProducto;
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
            formData.append('idRegistroAlmacen', idRegistro);
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
