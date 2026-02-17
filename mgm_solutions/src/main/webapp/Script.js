// notificaciones.js

document.addEventListener('DOMContentLoaded', function() {
    // 1. Capturar los parámetros de la URL
    const urlParams = new URLSearchParams(window.location.search);

    // Configuración común para SweetAlert2
    const toast = Swal.mixin({
        timer: 3500,
        timerProgressBar: true,
        showConfirmButton: true,
        confirmButtonColor: '#3085d6'
    });

    // 2. Lógica de mensajes según el parámetro recibido
    
    // --- CASOS DE ÉXITO ---
    if (urlParams.get('registro') === 'success') {
        Swal.fire({
            icon: 'success',
            title: '¡Registro Exitoso!',
            text: 'Tus datos han sido guardados. Ya puedes ingresar.',
            confirmButtonText: 'Entendido'
        });
    }

    // --- CASOS DE ERROR ---
    if (urlParams.get('loginError') === '1') {
        Swal.fire({
            icon: 'error',
            title: 'Acceso Denegado',
            text: 'El NIT o la contraseña son incorrectos.',
            footer: 'Asegúrate de que el Bloq Mayús esté desactivado'
        });
    }

    if (urlParams.get('error') === 'registro_fallido') {
        Swal.fire({
            icon: 'warning',
            title: 'No se pudo registrar',
            text: 'Es posible que el NIT o correo ya estén en uso.',
        });
    }

    // 3. Limpiar la URL para evitar que el mensaje se repita al recargar (F5)
    if (window.history.replaceState) {
        const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
        window.history.replaceState({path: cleanUrl}, '', cleanUrl);
    }
});