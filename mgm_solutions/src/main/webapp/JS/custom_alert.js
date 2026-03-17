(function() {
    let alertQueue = [];
    let isInitialized = false;
    let overlay, messageEl;

    // 1. Override window.alert IMMEDIATELY
    const nativeAlert = window.alert;
    window.alert = function(message) {
        if (isInitialized) {
            showCustomAlert(message);
        } else {
            alertQueue.push(message);
        }
        console.log("Custom alert intercepted:", message);
    };

    const showCustomAlert = (message) => {
        messageEl.textContent = message;
        overlay.style.display = 'flex';
        overlay.offsetHeight; // Force reflow
        overlay.classList.add('active');
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
                    <span class="custom-alert__title">Alerta:</span>
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
            setTimeout(() => { overlay.style.display = 'none'; }, 300);
        });

        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) btn.click();
        });

        isInitialized = true;
        
        // Process queued alerts
        while (alertQueue.length > 0) {
            showCustomAlert(alertQueue.shift());
        }
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAlertUI);
    } else {
        initAlertUI();
    }
})();
