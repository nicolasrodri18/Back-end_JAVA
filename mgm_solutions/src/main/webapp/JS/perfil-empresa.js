// perfil-empresa.js - MGM Solutions
document.addEventListener("DOMContentLoaded", function () {
    console.log("Perfil de Empresa cargado correctamente.");

    // Elementos Globales
    const navbarNombre = document.getElementById('navbar-nombre');
    // Usamos selectores más específicos para evitar colisiones
    const contenedorEmpleados = document.querySelector('.cuerpo--cards');
    const contenedorVentasArray = document.querySelectorAll('.cuerpo--cards'); // redundante pero para seguridad
    const productosCards = document.querySelector('.cuerpo__contenido__cards:not(.grid__lista)');

    // 1. Cargar Nombre en Navbar y Datos de Perfil
    function cargarHeader() {
        fetch("../../RelacionLaboralServlet?action=getPerfil")
            .then(res => res.json())
            .then(data => {
                if (navbarNombre) navbarNombre.textContent = `Hi, ${data.userName}`;
                const welcomeName = document.getElementById('welcome-name');
                if (welcomeName) welcomeName.textContent = data.userName;
            })
            .catch(err => console.error("Error al cargar header:", err));
    }

    // 2. Cargar Lista de Empleados
    function cargarEmpleados() {
        const titulo = document.querySelector('.cuerpo--texto');
        if (!contenedorEmpleados || !titulo || titulo.textContent !== "Lista Empleados") return;

        fetch("../../RelacionLaboralServlet?action=listarEmpleados")
            .then(res => res.json())
            .then(data => {
                if (data.length === 0) {
                    contenedorEmpleados.innerHTML = '<p style="text-align: center; width: 100%; grid-column: 1 / -1; padding: 2rem;">No tienes empleados vinculados actualmente.</p>';
                    return;
                }
                contenedorEmpleados.innerHTML = '';
                data.forEach(emp => {
                    const card = document.createElement('div');
                    card.className = 'card__lista';
                    card.innerHTML = `
                        <div class="card__lista--textos">
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">ID Registro:</h4>
                                <h5 class="card__lista--texto__descripcion">${emp.id}</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Documento:</h4>
                                <h5 class="card__lista--texto__descripcion">${emp.documento}</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Nombre:</h4>
                                <h5 class="card__lista--texto__descripcion">${emp.nombre}</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Correo:</h4>
                                <h5 class="card__lista--texto__descripcion">${emp.correo || "No registrado"}</h5>
                            </div>
                        </div>
                        <div class="card__lista--botones">
                            <label for="toggle-modulo-detalle" class="card__lista__boton--accion" onclick="verDetalleEmpleado('${encodeURIComponent(JSON.stringify(emp))}')">Detalles</label>
                            <label for="toggle-modulo-eliminar" class="card__lista__boton--accion" onclick="prepararEliminarEmpleado('${emp.id}')">Eliminar</label>
                        </div>
                    `;
                    contenedorEmpleados.appendChild(card);
                });
            })
            .catch(err => console.error("Error al cargar empleados:", err));
    }

    // 3. Cargar Lista de Ventas (Historial)
    function cargarVentas() {
        const titulo = document.querySelector('.cuerpo--texto');
        if (!contenedorEmpleados || !titulo || titulo.textContent !== "Registro de venta") return;

        fetch("../../VentasServlet?action=listarEmpresa")
            .then(res => res.json())
            .then(data => {
                if (!data || data.length === 0) {
                    contenedorEmpleados.innerHTML = '<p style="text-align: center; width: 100%; grid-column: 1 / -1; padding: 2rem;">No hay registros de ventas vinculados a tu empresa.</p>';
                    return;
                }
                contenedorEmpleados.innerHTML = '';
                data.forEach(venta => {
                    const card = document.createElement('div');
                    card.className = 'card__lista';
                    card.innerHTML = `
                        <div class="card__lista--textos">
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">ID Registro:</h4>
                                <h5 class="card__lista--texto__descripcion">${venta.idVenta}</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Empleado ID:</h4>
                                <h5 class="card__lista--texto__descripcion">${venta.empleadoId}</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Fecha:</h4>
                                <h5 class="card__lista--texto__descripcion">${venta.fecha}</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Producto:</h4>
                                <h5 class="card__lista--texto__descripcion">${venta.producto} (x${venta.cantidad})</h5>
                            </div>
                            <div class="card--texto">
                                <h4 class="card__lista--texto__propiedad">Monto:</h4>
                                <h5 class="card__lista--texto__descripcion">$${venta.total}</h5>
                            </div>
                        </div>
                        <div class="card__lista--botones">
                            <label for="toggle-modulo-detalle" class="card__lista__boton--accion" onclick="verDetalleVenta('${venta.idVenta}')">Detalles</label>
                        </div>
                    `;
                    contenedorEmpleados.appendChild(card);
                });
            })
            .catch(err => console.error("Error al cargar ventas:", err));
    }

    // 4. Cargar Lista de Productos (Inventario)
    function cargarProductos() {
        const titulo = document.querySelector('.cuerpo--texto');
        if (titulo && (titulo.textContent === "Lista Empleados" || titulo.textContent === "Registro de venta")) return;

        const contenedorProd = document.querySelector('.cuerpo__contenido__cards');
        if (!contenedorProd || contenedorProd.classList.contains('grid__lista')) return;

        fetch("../../InventarioServlet?action=listar")
            .then(res => res.json())
            .then(data => {
                if (!data || data.length === 0) {
                    contenedorProd.innerHTML = '<p style="text-align: center; width: 100%; padding: 2rem;">No tienes productos disponibles en tu inventario.</p>';
                    return;
                }
                contenedorProd.innerHTML = '';
                data.forEach(prod => {
                    const card = document.createElement('div');
                    card.className = 'card__producto';
                    card.innerHTML = `
                        <div class="card__producto--texto">
                            <h3 class="card__producto--texto__contenido">ID: ${prod.id}</h3>
                            <h3 class="card__producto--texto__contenido">Nombre: ${prod.nombre}</h3>
                            <h3 class="card__producto--texto__contenido">Stock: ${prod.stock}</h3>
                            <h3 class="card__producto--texto__contenido">Valor: $${prod.precio}</h3>
                        </div>
                        <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar" onclick="verDetalleProducto('${prod.id}', '${prod.nombre}', ${prod.stock}, ${prod.precio})">Seleccionar</label>
                    `;
                    contenedorProd.appendChild(card);
                });
            })
            .catch(err => console.error("Error al cargar productos:", err));
    }

    window.prepararEliminarEmpleado = function (id) {
        const btnConfirmar = document.getElementById('btn-confirmar-eliminar');
        if (btnConfirmar) {
            btnConfirmar.onclick = function () {
                fetch(`../../RelacionLaboralServlet?action=eliminarRelacion&idRelacion=${id}`, { method: 'POST' })
                    .then(res => res.json())
                    .then(data => {
                        alert(data.message);
                        if (data.status === "success") location.reload();
                    });
            };
        }
    };

    window.verDetalleProducto = function (id, nombre, stock, precio) {
        if (document.getElementById('det-prod-id')) document.getElementById('det-prod-id').textContent = id;
        if (document.getElementById('det-prod-nombre')) document.getElementById('det-prod-nombre').textContent = nombre;
        if (document.getElementById('det-prod-stock')) document.getElementById('det-prod-stock').textContent = stock;
        if (document.getElementById('det-prod-precio')) document.getElementById('det-prod-precio').textContent = precio;

        const btnEliminarProd = document.getElementById('btn-eliminar-producto');
        if (btnEliminarProd) {
            btnEliminarProd.onclick = function () {
                if (confirm("¿Estás seguro de eliminar este producto del inventario?")) {
                    fetch(`../../InventarioServlet?action=eliminar&id=${id}`, { method: 'POST' })
                        .then(res => res.json())
                        .then(data => {
                            alert(data.message);
                            if (data.status === "success") location.reload();
                        });
                }
            };
        }
    };

    window.verDetalleEmpleado = function (empJson) {
        const emp = JSON.parse(decodeURIComponent(empJson));
        const detalleContenido = document.getElementById('detalle-empleado-contenido');
        if (detalleContenido) {
            detalleContenido.innerHTML = `
                <p class="modulo__detalle--texto"><strong>ID Registro:</strong> ${emp.id}</p>
                <p class="modulo__detalle--texto"><strong>Nombre:</strong> ${emp.nombre}</p>
                <p class="modulo__detalle--texto"><strong>Documento:</strong> ${emp.documento}</p>
                <p class="modulo__detalle--texto"><strong>Correo:</strong> ${emp.correo || "No registrado"}</p>
                <div id="contenedor-estado">
                    <p class="modulo__detalle--texto"><strong>Estado:</strong> ${emp.estado}</p>
                    <button class="modulo__boton modulo__boton--seleccionar" style="margin-top:10px; width:auto; padding:5px 15px;" onclick="cambiarAEdicionEstado('${emp.id}', '${emp.estado}')">Editar Estado</button>
                </div>
                ${emp.direccion ? `<p class="modulo__detalle--texto"><strong>Dirección:</strong> ${emp.direccion}</p>` : ""}
            `;
        }
    };

    window.cambiarAEdicionEstado = function (id, estadoActual) {
        const contenedor = document.getElementById('contenedor-estado');
        if (contenedor) {
            contenedor.innerHTML = `
                <p class="modulo__detalle--texto"><strong>Estado:</strong></p>
                <select id="select-estado-${id}" class="modulo__input" style="margin-bottom:10px;">
                    <option value="Activo" ${estadoActual === 'Activo' ? 'selected' : ''}>Activo</option>
                    <option value="Inactivo" ${estadoActual === 'Inactivo' ? 'selected' : ''}>Inactivo</option>
                </select>
                <div style="display:flex; gap:10px;">
                    <button class="modulo__boton modulo__boton--agregar" style="width:auto; padding:5px 15px;" onclick="actualizarEstadoEmpleado('${id}')">Guardar</button>
                    <button class="modulo__boton modulo__boton--cancelar" style="width:auto; padding:5px 15px;" onclick="location.reload()">Cancelar</button>
                </div>
            `;
        }
    };

    window.actualizarEstadoEmpleado = function (id) {
        const nuevoEstado = document.getElementById(`select-estado-${id}`).value;
        const formData = new URLSearchParams();
        formData.append('idRelacion', id);
        formData.append('estado', nuevoEstado);

        fetch(`../../RelacionLaboralServlet?action=actualizarEstado`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
            .then(res => res.json())
            .then(data => {
                alert(data.message);
                if (data.status === "success") location.reload();
            })
            .catch(err => alert("Error al actualizar estado"));
    };

    window.verDetalleVenta = function (id) {
        console.log("Ver detalle de venta:", id);
    };

    const formProducto = document.getElementById('form-agregar-producto');
    if (formProducto) {
        formProducto.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new URLSearchParams();
            formData.append('id', document.getElementById('id-producto').value);
            formData.append('nombre', document.getElementById('nombre-producto').value);
            formData.append('stock', document.getElementById('stock').value);
            formData.append('precio', document.getElementById('valor-venta').value);

            fetch("../../InventarioServlet?action=agregar", {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
                .then(res => res.json())
                .then(data => {
                    alert(data.message);
                    if (data.status === "success") location.reload();
                })
                .catch(err => alert("Error al agregar producto"));
        });
    }

    const btnBuscar = document.getElementById('btn-buscar-empleado');
    if (btnBuscar) {
        btnBuscar.addEventListener('click', function () {
            const documento = document.getElementById('id-empleado').value;
            const resDiv = document.getElementById('resultado-busqueda');
            const msgDiv = document.getElementById('mensaje-busqueda');

            fetch(`../../RelacionLaboralServlet?action=buscarUsuario&documento=${documento}`)
                .then(res => res.json())
                .then(data => {
                    if (data.status === "error") {
                        msgDiv.textContent = data.message;
                        msgDiv.style.display = 'block';
                        resDiv.style.display = 'none';
                    } else {
                        document.getElementById('res-nombre').textContent = data.nombre;
                        document.getElementById('res-correo').textContent = data.correo || "No registrado";
                        document.getElementById('res-nit').value = data.documento;
                        resDiv.style.display = 'block';
                        msgDiv.style.display = 'none';
                    }
                });
        });
    }

    const btnInvitar = document.getElementById('btn-enviar-invitacion');
    if (btnInvitar) {
        btnInvitar.addEventListener('click', function () {
            const nitEmpleado = document.getElementById('res-nit').value;
            fetch(`../../RelacionLaboralServlet?action=invitarEmpleado&nitEmpleado=${nitEmpleado}`, { method: 'POST' })
                .then(res => res.json())
                .then(data => {
                    alert(data.message);
                    if (data.status === "success") location.reload();
                });
        });
    }

    cargarHeader();
    cargarEmpleados();
    cargarProductos();
    cargarVentas();
});
