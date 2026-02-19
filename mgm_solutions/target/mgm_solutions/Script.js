// =====================================================================
// Script.js - MGM Solutions
// Validación inline con fetch(): muestra SweetAlert + campo en rojo
// sin salir del formulario.
// =====================================================================

document.addEventListener("DOMContentLoaded", function () {

    // ----------------------------------------------------------------
    // CONFIGURACIÓN DE MENSAJES
    // ----------------------------------------------------------------

    // Cada error mapea: mensaje para SweetAlert + qué campo pintar en rojo
    // campo: ID del input en el HTML
    const erroresEmpresa = {
        nit_existente: {
            campo: "nit-empresa",
            titulo: "NIT ya registrado",
            texto: "El NIT ingresado ya está asociado a una empresa existente.",
            icono: "warning",
            color: "#FF9800"
        },
        correo_existente: {
            campo: "email",
            titulo: "Correo ya registrado",
            texto: "El correo electrónico ingresado ya está en uso. Usa uno diferente.",
            icono: "warning",
            color: "#FF9800"
        },
        empresa_existente: {
            campo: null,
            titulo: "Error del servidor",
            texto: "Ocurrió un problema al guardar los datos. Intenta de nuevo.",
            icono: "error",
            color: "#e53935"
        }
    };

    const erroresUsuario = {
        documento_existente: {
            campo: "doc-usuario",
            titulo: "Documento ya registrado",
            texto: "El número de documento ya pertenece a un usuario existente.",
            icono: "warning",
            color: "#FF9800"
        },
        correo_existente: {
            campo: "email-user",
            titulo: "Correo ya registrado",
            texto: "El correo electrónico ingresado ya está en uso. Usa uno diferente.",
            icono: "warning",
            color: "#FF9800"
        },
        registro_fallido: {
            campo: null,
            titulo: "Error del servidor",
            texto: "Ocurrió un problema al guardar los datos. Intenta de nuevo.",
            icono: "error",
            color: "#e53935"
        }
    };

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    /** Elimina el estilo de error de todos los campos del formulario */
    function limpiarErrores(form) {
        form.querySelectorAll(".campo_form").forEach(function (el) {
            el.classList.remove("campo-error");
        });
    }

    /** Pinta en rojo el campo con el id indicado */
    function marcarCampoError(fieldId) {
        if (!fieldId) return;
        var campo = document.getElementById(fieldId);
        if (campo) {
            campo.classList.add("campo-error");
            // Scroll suave hacia el campo con error
            campo.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    }

    /** Muestra el SweetAlert con la config del error */
    function mostrarAlerta(config) {
        Swal.fire({
            title: config.titulo,
            text: config.texto,
            icon: config.icono,
            confirmButtonText: "Corregir",
            confirmButtonColor: config.color,
            timer: 6000,
            timerProgressBar: true
        });
    }

    /** Muestra el SweetAlert de éxito y vuelve al panel de login */
    function mostrarExito(mensaje, toggleId) {
        Swal.fire({
            title: mensaje.titulo,
            text: mensaje.texto,
            icon: "success",
            confirmButtonText: "Iniciar Sesión",
            confirmButtonColor: "#4CAF50",
            timer: 5000,
            timerProgressBar: true
        }).then(function () {
            // Desmarcar el checkbox para volver a mostrar el formulario de login
            var toggle = document.getElementById(toggleId);
            if (toggle) toggle.checked = false;
        });
    }

    // ----------------------------------------------------------------
    // MANEJO DEL FORMULARIO DE REGISTRO EMPRESA
    // ----------------------------------------------------------------

    var formEmpresa = document.querySelector(".menu-registro-empresa--formulario");

    if (formEmpresa) {
        formEmpresa.addEventListener("submit", async function (e) {
            e.preventDefault(); // Evita la navegación/recarga
            limpiarErrores(formEmpresa);

            try {
                var resp = await fetch(formEmpresa.action, {
                    method: "POST",
                    body: new URLSearchParams(new FormData(formEmpresa)),
                    headers: { "X-Requested-With": "XMLHttpRequest" }
                });

                var data = await resp.json();

                if (data.status === "ok") {
                    formEmpresa.reset();
                    mostrarExito(
                        {
                            titulo: "¡Empresa registrada con éxito!",
                            texto: "Ya puedes iniciar sesión con las credenciales de tu empresa."
                        },
                        "toggle-registro__empresa"
                    );
                } else {
                    var info = erroresEmpresa[data.codigo] || erroresEmpresa["empresa_existente"];
                    marcarCampoError(info.campo);
                    mostrarAlerta(info);
                }

            } catch (err) {
                mostrarAlerta(erroresEmpresa["empresa_existente"]);
            }
        });
    }

    // ----------------------------------------------------------------
    // MANEJO DEL FORMULARIO DE REGISTRO USUARIO
    // ----------------------------------------------------------------

    var formUsuario = document.querySelector(".menu-registro-usuario");

    if (formUsuario) {
        formUsuario.addEventListener("submit", async function (e) {
            e.preventDefault();
            limpiarErrores(formUsuario);

            try {
                var resp = await fetch(formUsuario.action, {
                    method: "POST",
                    body: new URLSearchParams(new FormData(formUsuario)),
                    headers: { "X-Requested-With": "XMLHttpRequest" }
                });

                var data = await resp.json();

                if (data.status === "ok") {
                    formUsuario.reset();
                    mostrarExito(
                        {
                            titulo: "¡Usuario registrado con éxito!",
                            texto: "Ya puedes iniciar sesión con tus datos."
                        },
                        "toggle-registro__usuario"
                    );
                } else {
                    var info = erroresUsuario[data.codigo] || erroresUsuario["registro_fallido"];
                    marcarCampoError(info.campo);
                    mostrarAlerta(info);
                }

            } catch (err) {
                mostrarAlerta(erroresUsuario["registro_fallido"]);
            }
        });
    }

    // ----------------------------------------------------------------
    // RESTRICCIONES DE TECLADO EN TIEMPO REAL
    // ----------------------------------------------------------------

    // Campo Documento (registro usuario): solo dígitos, 8-10 caracteres
    var campoDocumento = document.getElementById("doc-usuario");
    if (campoDocumento) {
        campoDocumento.addEventListener("keypress", function (e) {
            // Permitir solo 0-9; bloquear cualquier otra tecla
            if (!/[0-9]/.test(e.key) && !["Backspace", "Tab", "ArrowLeft", "ArrowRight", "Delete"].includes(e.key)) {
                e.preventDefault();
            }
        });

        // Bloqueo por si pegan texto con caracteres inválidos
        campoDocumento.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, "");
        });
    }

    // Campo NIT (registro empresa): solo dígitos y guión (-), 11-13 caracteres
    var campoNit = document.getElementById("nit-empresa");
    if (campoNit) {
        campoNit.addEventListener("keypress", function (e) {
            // Permitir 0-9 y guión "-"
            if (!/[0-9\-]/.test(e.key) && !["Backspace", "Tab", "ArrowLeft", "ArrowRight", "Delete"].includes(e.key)) {
                e.preventDefault();
            }
        });

        // Bloqueo por si pegan texto con caracteres inválidos
        campoNit.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9\-]/g, "");
        });
    }

    // ----------------------------------------------------------------
    // MANEJO DEL FORMULARIO DE LOGIN
    // ----------------------------------------------------------------

    var erroresLogin = {
        contrasena_incorrecta: {
            campo: "password",
            titulo: "Contraseña incorrecta",
            texto: "La contraseña ingresada no coincide con el usuario. Verifica e intenta de nuevo.",
            icono: "warning",
            color: "#FF9800"
        },
        usuario_no_encontrado: {
            campo: "nit-documento",
            titulo: "Usuario no encontrado",
            texto: "El NIT o documento ingresado no está registrado en el sistema.",
            icono: "warning",
            color: "#FF9800"
        },
        error_servidor: {
            campo: null,
            titulo: "Error del servidor",
            texto: "No se pudo conectar con la base de datos. Intenta de nuevo más tarde.",
            icono: "error",
            color: "#e53935"
        }
    };

    var formLogin = document.querySelector(".menu-inicio--formulario");

    if (formLogin) {
        formLogin.addEventListener("submit", async function (e) {
            e.preventDefault();
            limpiarErrores(formLogin);

            try {
                var resp = await fetch(formLogin.action, {
                    method: "POST",
                    body: new URLSearchParams(new FormData(formLogin)),
                    headers: { "X-Requested-With": "XMLHttpRequest" }
                });

                var data = await resp.json();

                if (data.status === "ok") {
                    // Mostrar mensaje de bienvenida exitoso
                    Swal.fire({
                        title: "¡Bienvenida exitosa!",
                        html: `Hola <b>${data.nombre}</b>, has iniciado sesión como <b>${data.rol}</b>`,
                        icon: "success",
                        confirmButtonText: "Continuar",
                        confirmButtonColor: "#4CAF50",
                        timer: 3000,
                        timerProgressBar: true
                    }).then(function () {
                        // El servlet ya guardó la sesión; navegamos a la URL devuelta
                        window.location.href = data.redirect;
                    });
                } else {
                    var info = erroresLogin[data.codigo] || erroresLogin["error_servidor"];
                    marcarCampoError(info.campo);
                    mostrarAlerta(info);
                }

            } catch (err) {
                mostrarAlerta(erroresLogin["error_servidor"]);
            }
        });
    }

});
