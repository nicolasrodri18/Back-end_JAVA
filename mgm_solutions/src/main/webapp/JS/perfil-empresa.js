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
    /** 
     * Solicita los datos del perfil actual (Nombre, Rol) para actualizar el header.
     */
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

    // Variable para almacenar todos los empleados cargados
    let todosLosEmpleados = [];

    // Función para renderizar una lista de empleados en las cards
    function renderEmpleados(lista) {
        if (!contenedorEmpleados) return;
        if (lista.length === 0) {
            contenedorEmpleados.innerHTML = '<p style="text-align: center; width: 100%; grid-column: 1 / -1; padding: 2rem;">No se encontraron empleados con ese criterio.</p>';
            return;
        }
        contenedorEmpleados.innerHTML = '';
        lista.forEach(emp => {
            const card = document.createElement('div');
            const estadoClass = emp.estado === 'Activo' ? 'card__lista--activo' : 'card__lista--inactivo';
            card.className = `card__lista ${estadoClass}`;
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
                    <div class="card--texto">
                        <h4 class="card__lista--texto__propiedad">Estado:</h4>
                        <h5 class="card__lista--texto__descripcion">${emp.estado}</h5>
                    </div>
                </div>
                <div class="card__lista--botones">
                    <label for="toggle-modulo-detalle" class="card__lista__boton--accion" onclick="verDetalleEmpleado('${encodeURIComponent(JSON.stringify(emp))}')">Detalles</label>
                    <label for="toggle-modulo-eliminar" class="card__lista__boton--accion" onclick="prepararEliminarEmpleado('${emp.id}')">Eliminar</label>
                </div>
            `;
            contenedorEmpleados.appendChild(card);
        });
    }

    // 2. Cargar Lista de Empleados
    /**
     * Obtiene y renderiza la lista de empleados vinculados a la empresa.
     */
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
                todosLosEmpleados = data;
                renderEmpleados(todosLosEmpleados);
                inicializarBusqueda();
            })
            .catch(err => console.error("Error al cargar empleados:", err));
    }

    // Función de búsqueda en tiempo real
    function inicializarBusqueda() {
        const formBusqueda = document.querySelector('.cuerpo__Barra__busqueda');
        const inputBusqueda = document.querySelector('.cuerpo__input--busqueda');
        const btnBuscarLista = document.getElementById('btn-buscar-lista');
        if (!inputBusqueda) return;

        // Función que ejecuta el filtrado
        function filtrarEmpleados() {
            const query = inputBusqueda.value.toLowerCase().trim();
            if (query === '') {
                renderEmpleados(todosLosEmpleados);
                return;
            }
            const filtrados = todosLosEmpleados.filter(emp => {
                return (
                    String(emp.id).toLowerCase().includes(query) ||
                    String(emp.documento).toLowerCase().includes(query) ||
                    String(emp.nombre).toLowerCase().includes(query) ||
                    String(emp.correo || '').toLowerCase().includes(query) ||
                    String(emp.estado).toLowerCase().startsWith(query)
                );
            });
            renderEmpleados(filtrados);
        }

        // Evitar que el formulario recargue la página
        if (formBusqueda) {
            formBusqueda.addEventListener('submit', function (e) {
                e.preventDefault();
                filtrarEmpleados();
            });
        }

        // Filtrar en tiempo real mientras el usuario escribe
        inputBusqueda.addEventListener('input', filtrarEmpleados);

        // Filtrar al hacer clic en el botón de la lupa
        if (btnBuscarLista) {
            btnBuscarLista.addEventListener('click', filtrarEmpleados);
        }
    }

    // Variable para almacenar todas las ventas cargadas
    let todasLasVentas = [];

    // Función para renderizar una lista de ventas en las cards
    function renderVentas(lista) {
        if (!contenedorEmpleados) return;
        if (lista.length === 0) {
            contenedorEmpleados.innerHTML = '<p style="text-align: center; width: 100%; grid-column: 1 / -1; padding: 2rem;">No se encontraron ventas con ese criterio.</p>';
            return;
        }
        contenedorEmpleados.innerHTML = '';
        // Obtener fecha local en formato YYYY-MM-DD
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const today = `${year}-${month}-${day}`;

        lista.forEach(venta => {
            const card = document.createElement('div');
            card.className = 'card__lista';

            // Resaltar si es de hoy
            if (venta.fecha === today) {
                card.style.border = "2px solid #ff9800"; // Naranja
                card.style.boxShadow = "0 0 10px rgba(255, 152, 0, 0.2)";
            }
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
                        <h4 class="card__lista--texto__propiedad">Monto Total:</h4>
                        <h5 class="card__lista--texto__descripcion">$${venta.total}</h5>
                    </div>
                    <div class="card--texto">
                        <h4 class="card__lista--texto__propiedad">Precio Unitario:</h4>
                        <h5 class="card__lista--texto__descripcion">$${venta.precioUnitario}</h5>
                    </div>
                </div>
                <div class="card__lista--botones">
                    <label for="toggle-modulo-detalle" class="card__lista__boton--accion" onclick="verDetalleVenta('${venta.idVenta}')">Detalles</label>
                </div>
            `;
            contenedorEmpleados.appendChild(card);
        });
    }

    // Función de búsqueda para ventas
    function inicializarBusquedaVentas() {
        const formBusqueda = document.querySelector('.cuerpo__Barra__busqueda');
        const inputBusqueda = document.querySelector('.cuerpo__input--busqueda');
        const btnBuscarVentas = document.getElementById('btn-buscar-ventas');
        if (!inputBusqueda) return;

        function filtrarVentas() {
            const query = inputBusqueda.value.toLowerCase().trim();
            if (query === '') {
                renderVentas(todasLasVentas);
                return;
            }
            const filtradas = todasLasVentas.filter(venta => {
                return (
                    String(venta.idVenta).toLowerCase().includes(query) ||
                    String(venta.empleadoId).toLowerCase().includes(query) ||
                    String(venta.fecha || '').toLowerCase().includes(query) ||
                    String(venta.producto || '').toLowerCase().includes(query) ||
                    String(venta.cantidad || '').toLowerCase().includes(query) ||
                    String(venta.total || '').toLowerCase().includes(query)
                );
            });
            renderVentas(filtradas);
        }

        // Evitar que el formulario recargue la página
        if (formBusqueda) {
            formBusqueda.addEventListener('submit', function (e) {
                e.preventDefault();
                filtrarVentas();
            });
        }

        // Filtrar en tiempo real mientras el usuario escribe
        inputBusqueda.addEventListener('input', filtrarVentas);

        // Filtrar al hacer clic en el botón de la lupa
        if (btnBuscarVentas) {
            btnBuscarVentas.addEventListener('click', filtrarVentas);
        }
    }

    // 3. Cargar Lista de Ventas (Historial)
    /**
     * Obtiene el historial de ventas registradas por los empleados de la empresa.
     */
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
                todasLasVentas = data;
                renderVentas(todasLasVentas);
                inicializarBusquedaVentas();
            })
            .catch(err => console.error("Error al cargar ventas:", err));
    }

    // Variable para almacenar todos los productos cargados
    let todosLosProductos = [];

    // Función para renderizar una lista de productos en las cards
    function renderProductos(lista, contenedor) {
        if (!contenedor) return;
        if (lista.length === 0) {
            contenedor.innerHTML = '<p style="text-align: center; width: 100%; padding: 2rem;">No se encontraron productos con ese criterio.</p>';
            return;
        }
        contenedor.innerHTML = '';
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        lista.forEach(prod => {
            const card = document.createElement('div');
            card.className = 'card__producto';
            // Ajustes de diseño similares al perfil de empleado
            card.style.minWidth = '220px';
            card.style.padding = '1.2rem';

            let vencimientoTexto = "";
            let vencimientoColor = "inherit";
            let isVencido = false;

            if (prod.vencimiento && prod.vencimiento !== "null") {
                const fechaVenc = new Date(prod.vencimiento + "T00:00:00");
                fechaVenc.setHours(0, 0, 0, 0);

                if (fechaVenc < hoy) {
                    isVencido = true;
                    vencimientoTexto = `Vencido desde: ${prod.vencimiento}`;
                    vencimientoColor = "#ff4d4d"; // Rojo
                } else if (fechaVenc.getTime() === hoy.getTime()) {
                    vencimientoTexto = "¡Vence Hoy!";
                    vencimientoColor = "#ffd700"; // Amarillo (Gold)
                } else {
                    vencimientoTexto = `Vence: ${prod.vencimiento}`;
                }
            }

            card.innerHTML = `
                <div class="card__producto--texto">
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.9rem;">ID Producto: ${prod.idProducto}</h3>
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.95rem; font-weight: bold; margin-bottom: 3px;">${prod.nombre}</h3>
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.9rem;">Stock: ${prod.stock}</h3>
                    <h3 class="card__producto--texto__contenido" style="font-size: 0.9rem;">Valor: $${prod.precio}</h3>
                    ${vencimientoTexto ? `<h4 class="card__producto--texto__contenido" style="color: ${vencimientoColor}; font-weight: bold; font-size: 0.8rem; margin-top: 3px; line-height: 1.1;">${vencimientoTexto}</h4>` : ''}
                </div>
                <div style="margin-top: 10px; display: flex; justify-content: center;">
                    <label for="toggle-modulo-seleccionar" class="card__producto--boton-seleccionar" 
                           style="font-size: 0.85rem; padding: 0.4rem 0.8rem; ${isVencido ? 'background-color: #721c24; color: #f8d7da; border-color: #721c24;' : ''}"
                           onclick="verDetalleProducto('${encodeURIComponent(JSON.stringify(prod))}')">
                        ${isVencido ? 'VENCIDO - Ver Detalle' : 'Seleccionar'}
                    </label>
                </div>
            `;
            contenedor.appendChild(card);
        });
    }

    // Función de búsqueda para productos
    function inicializarBusquedaProductos(contenedor) {
        const formBusqueda = document.querySelector('.cuerpo__Barra__busqueda');
        const inputBusqueda = document.querySelector('.cuerpo__input--busqueda');
        const btnBuscarProd = document.getElementById('btn-buscar-productos');
        if (!inputBusqueda) return;

        function filtrarProductos() {
            const query = inputBusqueda.value.toLowerCase().trim();
            if (query === '') {
                renderProductos(todosLosProductos, contenedor);
                return;
            }
            const filtrados = todosLosProductos.filter(prod => {
                return (
                    String(prod.idProducto).toLowerCase().includes(query) ||
                    String(prod.nombre || '').toLowerCase().includes(query) ||
                    String(prod.stock || '').toLowerCase().includes(query) ||
                    String(prod.precio || '').toLowerCase().includes(query) ||
                    String(prod.precioCompra || '').toLowerCase().includes(query) ||
                    String(prod.vencimiento || '').toLowerCase().includes(query)
                );
            });
            renderProductos(filtrados, contenedor);
        }

        // Evitar que el formulario recargue la página
        if (formBusqueda) {
            formBusqueda.addEventListener('submit', function (e) {
                e.preventDefault();
                filtrarProductos();
            });
        }

        // Filtrar en tiempo real mientras el usuario escribe
        inputBusqueda.addEventListener('input', filtrarProductos);

        // Filtrar al hacer clic en el botón de la lupa
        if (btnBuscarProd) {
            btnBuscarProd.addEventListener('click', filtrarProductos);
        }
    }

    // 4. Cargar Lista de Productos (Inventario)
    /**
     * Obtiene la lista de productos disponibles en el almacén de la empresa.
     * Identifica automáticamente productos vencidos.
     */
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
                todosLosProductos = data;
                renderProductos(todosLosProductos, contenedorProd);
                inicializarBusquedaProductos(contenedorProd);
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

    window.verDetalleProducto = function (prodJson) {
        const prod = JSON.parse(decodeURIComponent(prodJson));
        if (document.getElementById('det-prod-id')) document.getElementById('det-prod-id').textContent = prod.idProducto;
        if (document.getElementById('det-prod-nombre')) document.getElementById('det-prod-nombre').textContent = prod.nombre;
        if (document.getElementById('det-prod-stock')) document.getElementById('det-prod-stock').textContent = prod.stock;
        if (document.getElementById('det-prod-precio')) document.getElementById('det-prod-precio').textContent = prod.precio;
        if (document.getElementById('det-prod-compra')) document.getElementById('det-prod-compra').textContent = prod.precioCompra || "---";
        if (document.getElementById('det-prod-ganancia')) document.getElementById('det-prod-ganancia').textContent = prod.ganancia ? prod.ganancia + "%" : "---";
        if (document.getElementById('det-prod-vencimiento')) document.getElementById('det-prod-vencimiento').textContent = prod.vencimiento || "---";

        const btnEliminarProd = document.getElementById('btn-eliminar-producto');
        if (btnEliminarProd) {
            btnEliminarProd.onclick = function () {
                if (confirm("¿Estás seguro de eliminar este producto totalmente? Esto borrará el registro de almacén y el ID del producto.")) {
                    fetch(`../../InventarioServlet?action=eliminar&id=${prod.idRegistro}`, { method: 'POST' })
                        .then(res => res.json())
                        .then(data => {
                            alert(data.message);
                            if (data.status === "success") location.reload();
                        });
                }
            };
        }

        const btnAbrirEditar = document.getElementById('btn-abrir-editar');
        if (btnAbrirEditar) {
            btnAbrirEditar.onclick = function () {
                // Poblar el formulario de edición antes de abrir el modal (el label ya lo abre vía toggle)
                document.getElementById('edit-registro-id').value = prod.idRegistro;
                document.getElementById('edit-id-producto').value = prod.idProducto;
                document.getElementById('edit-nombre-producto').value = prod.nombre;
                document.getElementById('edit-stock').value = prod.stock;
                document.getElementById('edit-valor-venta').value = prod.precio;
                document.getElementById('edit-precio-compra').value = prod.precioCompra || '';
                document.getElementById('edit-porcentaje-ganancia').value = prod.ganancia || '';
                document.getElementById('edit-fecha-vencimiento').value = prod.vencimiento !== "null" ? prod.vencimiento : '';

                // Cerrar el modal de detalle al abrir el de edición
                const toggleDetalle = document.getElementById('toggle-modulo-seleccionar');
                if (toggleDetalle) toggleDetalle.checked = false;
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
            .then(async data => {
                await alert(data.message);
                if (data.status === "success") location.reload();
            })
            .catch(async err => await alert("Error al actualizar estado"));
    };

    window.verDetalleVenta = function (id) {
        const venta = todasLasVentas.find(v => String(v.idVenta) === String(id));
        const detalleContenido = document.getElementById('detalle-venta-contenido');

        const now = new Date();
        const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

        if (venta && detalleContenido) {
            let html = `
                <p class="modulo__detalle--texto"><strong>ID Registro Venta:</strong> ${venta.idVenta}</p>
                <p class="modulo__detalle--texto"><strong>Fecha:</strong> ${venta.fecha}</p>
                <hr style="border: 0; border-top: 1px solid rgba(255,255,255,0.1); margin: 10px 0;">
                <p class="modulo__detalle--texto"><strong>Empleado:</strong> ${venta.empleadoNombre}</p>
                <p class="modulo__detalle--texto"><strong>ID Empleado:</strong> ${venta.empleadoId}</p>
                <hr style="border: 0; border-top: 1px solid rgba(255,255,255,0.1); margin: 10px 0;">
                <p class="modulo__detalle--texto"><strong>Producto Vendido:</strong> ${venta.producto}</p>
                <p class="modulo__detalle--texto"><strong>Cantidad:</strong> ${venta.cantidad} unidades</p>
                <p class="modulo__detalle--texto"><strong>Precio Unitario:</strong> $${venta.precioUnitario}</p>
                <p class="modulo__detalle--texto" style="font-size: 1.2rem; margin-top: 10px; color: var(--color_primario);"><strong>Total:</strong> $${venta.total}</p>
            `;

            // Si es de hoy, agregar botón de eliminar
            if (venta.fecha === today) {
                html += `
                    <div style="margin-top: 20px; display: flex; justify-content: center;">
                        <button class="modulo__boton modulo__boton--eliminar" onclick="eliminarVentaHoy('${venta.idVenta}')" style="background-color: transparent; border: 2px solid #ff9800; color: #ff9800;">
                            Eliminar Venta
                        </button>
                    </div>
                `;
            }

            detalleContenido.innerHTML = html;
        } else if (detalleContenido) {
            detalleContenido.innerHTML = '<p class="modulo__detalle--texto">No se encontro informacion de esta venta.</p>';
        }
    };

    window.eliminarVentaHoy = function (id) {
        if (confirm("¿Estás seguro de eliminar esta venta? El stock se devolvera al almacen automaticamente.")) {
            fetch(`../../VentasServlet?action=eliminar&id=${id}`, { method: 'POST' })
                .then(res => res.json())
                .then(data => {
                    alert(data.message);
                    if (data.status === "success") location.reload();
                })
                .catch(err => {
                    console.error("Error al eliminar venta:", err);
                    alert("Error de conexión al eliminar la venta.");
                });
        }
    };

    // Modal Editar Producto - Lógica
    const formEditar = document.getElementById('form-editar-producto');
    if (formEditar) {
        const editPrecioVenta = document.getElementById('edit-valor-venta');
        const editPrecioCompra = document.getElementById('edit-precio-compra');
        const editPorcentaje = document.getElementById('edit-porcentaje-ganancia');

        function calcularPorcentajeEdit() {
            if (editPrecioVenta && editPrecioCompra && editPorcentaje) {
                const venta = parseFloat(editPrecioVenta.value);
                const compra = parseFloat(editPrecioCompra.value);
                if (!isNaN(venta) && !isNaN(compra) && compra > 0) {
                    const porcentaje = ((venta - compra) / compra) * 100;
                    editPorcentaje.value = porcentaje.toFixed(2);
                } else {
                    editPorcentaje.value = '';
                }
            }
        }

        if (editPrecioVenta) editPrecioVenta.addEventListener('input', calcularPorcentajeEdit);
        if (editPrecioCompra) editPrecioCompra.addEventListener('input', calcularPorcentajeEdit);

        formEditar.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new URLSearchParams();
            formData.append('idRegistro', document.getElementById('edit-registro-id').value);
            formData.append('idProducto', document.getElementById('edit-id-producto').value);
            formData.append('nombre', document.getElementById('edit-nombre-producto').value);
            formData.append('stock', document.getElementById('edit-stock').value);
            formData.append('precio', document.getElementById('edit-valor-venta').value);

            const pc = document.getElementById('edit-precio-compra').value;
            const pg = document.getElementById('edit-porcentaje-ganancia').value;
            const fv = document.getElementById('edit-fecha-vencimiento').value;

            if (pc) formData.append('precioCompra', pc);
            if (pg) formData.append('porcentajeGanancia', pg);
            if (fv) formData.append('fechaVencimiento', fv);

            fetch("../../InventarioServlet?action=editar", {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
                .then(res => res.json())
                .then(data => {
                    alert(data.message);
                    if (data.status === "success") location.reload();
                })
                .catch(err => alert("Error al actualizar producto"));
        });
    }

    const formProducto = document.getElementById('form-agregar-producto');
    if (formProducto) {
        // Auto-calcular porcentaje de ganancia cuando cambian los precios
        const inputIdProducto = document.getElementById('id-producto');
        const inputPrecioVenta = document.getElementById('valor-venta');

        // Restricción de 9 dígitos en tiempo real
        if (inputIdProducto) {
            inputIdProducto.addEventListener('input', function () {
                if (this.value.length > 9) {
                    this.value = this.value.slice(0, 9);
                }
            });
        }
        const inputPrecioCompra = document.getElementById('precio-compra');
        const inputPorcentaje = document.getElementById('porcentaje-ganancia');

        function calcularPorcentaje() {
            if (inputPrecioVenta && inputPrecioCompra && inputPorcentaje) {
                const venta = parseFloat(inputPrecioVenta.value);
                const compra = parseFloat(inputPrecioCompra.value);
                if (!isNaN(venta) && !isNaN(compra) && compra > 0) {
                    const porcentaje = ((venta - compra) / compra) * 100;
                    inputPorcentaje.value = porcentaje.toFixed(2);
                } else {
                    inputPorcentaje.value = '';
                }
            }
        }

        if (inputPrecioVenta) inputPrecioVenta.addEventListener('input', calcularPorcentaje);
        if (inputPrecioCompra) inputPrecioCompra.addEventListener('input', calcularPorcentaje);

        /**
         * Maneja el envío del formulario para agregar un nuevo producto.
         * Incluye validación de ID (9 dígitos) y campos obligatorios.
         */
        formProducto.addEventListener('submit', function (e) {
            e.preventDefault();
            const idVal = document.getElementById('id-producto').value;
            if (idVal.length > 9) {
                alert("El ID del producto no puede exceder los 9 dígitos.");
                return;
            }
            const formData = new URLSearchParams();
            formData.append('id', idVal);
            formData.append('nombre', document.getElementById('nombre-producto').value);
            formData.append('stock', document.getElementById('stock').value);
            formData.append('precio', document.getElementById('valor-venta').value);

            // Nuevos campos
            const precioCompra = document.getElementById('precio-compra');
            const porcentajeGanancia = document.getElementById('porcentaje-ganancia');
            const fechaVencimiento = document.getElementById('fecha-vencimiento');

            if (precioCompra && precioCompra.value) formData.append('precioCompra', precioCompra.value);
            if (porcentajeGanancia && porcentajeGanancia.value) formData.append('porcentajeGanancia', porcentajeGanancia.value);
            if (fechaVencimiento && fechaVencimiento.value) formData.append('fechaVencimiento', fechaVencimiento.value);

            fetch("../../InventarioServlet?action=agregar", {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
                .then(res => res.json())
                .then(async data => {
                    await alert(data.message);
                    if (data.status === "success") location.reload();
                })
                .catch(async err => await alert("Error al agregar producto"));
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
