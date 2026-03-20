document.addEventListener('DOMContentLoaded', function () {
    const navbarNombre = document.getElementById('navbar-nombre');
    const ciaNit = document.getElementById('cia-nit');
    const ciaNombre = document.getElementById('cia-nombre');
    const ciaEmail = document.getElementById('cia-email');
    const ciaDirec = document.getElementById('cia-direc');
    const ciaCiudad = document.getElementById('cia-ciudad');

    function cargarPerfil() {
        fetch("../../RelacionLaboralServlet?action=getPerfil")
            .then(res => res.json())
            .then(data => {
                if (navbarNombre) navbarNombre.textContent = `Hola, ${data.userName}`;
                if (ciaNit) ciaNit.textContent = data.userDoc;
                if (ciaNombre) ciaNombre.textContent = data.userName;
                if (ciaEmail) ciaEmail.textContent = data.userEmail || "No registrado";
                if (ciaDirec) ciaDirec.textContent = data.userDirec || "No registrada";
                if (ciaCiudad) ciaCiudad.textContent = data.ciaCiudad || "No registrada";
            });
    }

    /**
     * Carga dinámicamente el selector de ciudades para el formulario de edición.
     * @param {string} selectedCityName Nombre de la ciudad actual para marcarla como seleccionada.
     */
    function cargarCiudades(selectedCityName) {
        const selectCiudad = document.getElementById('edit-cia-ciudad');
        if (!selectCiudad) return;

        fetch("../../RelacionLaboralServlet?action=listarCiudades")
            .then(res => res.json())
            .then(ciudades => {
                selectCiudad.innerHTML = '';
                ciudades.forEach(c => {
                    const opt = document.createElement('option');
                    opt.value = c.id;
                    opt.textContent = c.nombre;
                    if (c.nombre === selectedCityName) opt.selected = true;
                    selectCiudad.appendChild(opt);
                });
            });
    }

    function abrirModalEdicion() {
        const nombreInput = document.getElementById('edit-cia-nombre');
        const emailInput = document.getElementById('edit-cia-email');
        const direcInput = document.getElementById('edit-cia-direc');
        const passInput = document.getElementById('edit-cia-pass');

        if (nombreInput) nombreInput.value = ciaNombre.textContent;
        if (emailInput) emailInput.value = ciaEmail.textContent !== "No registrado" ? ciaEmail.textContent : "";
        if (direcInput) direcInput.value = ciaDirec.textContent !== "No registrada" ? ciaDirec.textContent : "";
        if (passInput) passInput.value = "";

        cargarCiudades(ciaCiudad.textContent);
    }

    const toggleEditar = document.getElementById('toggle-modulo-editar-perfil');
    const btnAbrirEditar = document.getElementById('btn-abrir-editar-perfil');

    if (btnAbrirEditar && toggleEditar) {
        btnAbrirEditar.addEventListener('click', function (e) {
            e.preventDefault();
            toggleEditar.checked = true;
            abrirModalEdicion();
            document.getElementById('modal-editar-perfil').style.display = 'flex';
        });
    }

    if (toggleEditar) {
        toggleEditar.addEventListener('change', function () {
            if (this.checked) {
                abrirModalEdicion();
                document.getElementById('modal-editar-perfil').style.display = 'flex';
            } else {
                document.getElementById('modal-editar-perfil').style.display = 'none';
            }
        });
    }

    const formEditarPerfil = document.getElementById('form-editar-perfil');
    if (formEditarPerfil) {
        formEditarPerfil.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new URLSearchParams();
            formData.append('nombre', document.getElementById('edit-cia-nombre').value);
            formData.append('email', document.getElementById('edit-cia-email').value);
            formData.append('direccion', document.getElementById('edit-cia-direc').value);
            formData.append('ciudad', document.getElementById('edit-cia-ciudad').value);
            formData.append('pass', document.getElementById('edit-cia-pass').value);

            fetch("../../RelacionLaboralServlet?action=actualizarPerfilEmpresa", {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
                .then(res => res.json())
                .then(data => {
                    alert(data.message);
                    if (data.status === "success") location.reload();
                })
                .catch(err => alert("Error al actualizar datos de empresa."));
        });
    }

    cargarPerfil();
});
