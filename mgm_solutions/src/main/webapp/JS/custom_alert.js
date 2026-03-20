(function() {
    let alertQueue = [];
    let isInitialized = false;
    let overlay, messageEl;
    let resolveCurrentAlert = null;

    // 1. Override window.alert IMMEDIATELY
    /**
     * Sobrescribe la función nativa window.alert para usar un modal personalizado.
     * Esta versión devuelve una Promesa, lo que permite usar 'await alert(...)' 
     * para detener la ejecución hasta que el usuario cierre el modal.
     */
    const nativeAlert = window.alert;
    window.alert = function(message) {
        console.log("%c[CustomAlert] Intercepted:", "color: #0A7917; font-weight: bold;", message);
        // Devuelve una promesa para que el código llamante pueda usar 'await alert(...)'.
        return new Promise((resolve) => {
            // Si el DOM y los elementos del modal ya están creados, muestra el alerta.
            if (isInitialized && overlay && messageEl) {
                showCustomAlert(message, resolve);
            } else {
                // Si se llama antes de cargar el DOM, se guarda en cola para mostrarse después.
                console.warn("[CustomAlert] Not initialized yet, queuing message:", message);
                alertQueue.push({ message, resolve });
            }
        });
    };

    const showCustomAlert = (message, resolve) => {
        // Almacena la función resolve para ejecutarla cuando el usuario haga clic en 'Aceptar'.
        resolveCurrentAlert = resolve;
        // Inserta el mensaje en el cuerpo del modal.
        messageEl.textContent = message;
        // Hace visible el contenedor del modal usando display flex.
        overlay.style.display = 'flex';
        overlay.offsetHeight; // Truco de 'reflow' para asegurar que la animación de CSS se ejecute.
        overlay.classList.add('active'); // Aplica la clase que activa la opacidad y escala.
    };

    const initAlertUI = () => {
        if (isInitialized) return;

        // Add CSS if not already present (failsafe)
        if (!document.getElementById('custom-alert-styles')) {
            const link = document.createElement('link');
            link.id = 'custom-alert-styles';
            link.rel = 'stylesheet';
            link.href = (window.location.pathname.includes('/JSPS/') ? '../../' : '') + 'CSS/components/custom_alert.css';
            document.head.appendChild(link);
        }

        const alertHtml = `
            <div id="custom-alert-overlay" class="custom-alert__overlay">
                <div class="custom-alert__modal">
                    <span class="custom-alert__title">Recibido:</span>
                    <p id="custom-alert-message" class="custom-alert__message"></p>
                    <button id="custom-alert-btn" class="custom-alert__button">Aceptar</button>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', alertHtml);

        overlay = document.getElementById('custom-alert-overlay');
        messageEl = document.getElementById('custom-alert-message');
        const btn = document.getElementById('custom-alert-btn');

        btn.addEventListener('click', () => {
            overlay.classList.remove('active');
            setTimeout(() => { 
                overlay.style.display = 'none'; 
                if (resolveCurrentAlert) {
                    resolveCurrentAlert();
                    resolveCurrentAlert = null;
                }
            }, 300);
        });

        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) btn.click();
        });

        isInitialized = true;
        
        while (alertQueue.length > 0) {
            const item = alertQueue.shift();
            showCustomAlert(item.message, item.resolve);
        }
    };

    console.log("%c[CustomAlert] Script loaded and window.alert overwritten", "color: #0A7917; font-weight: bold;");

    if (document.readyState === 'complete' || document.readyState === 'interactive') {
        initAlertUI();
    } else {
        document.addEventListener('DOMContentLoaded', initAlertUI);
    }
})();
