// =================== CONFIGURACIÓN FIREBASE ===================
const firebaseConfig = {
    apiKey: "AIzaSyCaycR8mbrfm7xI4yLH-FoHGtsb7J15VI0",
    authDomain: "scanwatter-1bf04.firebaseapp.com",
    databaseURL: "https://scanwatter-1bf04-default-rtdb.firebaseio.com",
    projectId: "scanwatter-1bf04",
    storageBucket: "scanwatter-1bf04.firebasestorage.app",
    messagingSenderId: "19246885609",
    appId: "1:19246885609:web:c50bc7012698ddfcddde78",
    measurementId: "G-GCR3RHEQQQ"
};
firebase.initializeApp(firebaseConfig);

// =================== VALIDACIONES ===================
function validarEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// ... (Implementar aquí el resto de validaciones si es necesario) ...

// =================== EVENTOS ===================
document.addEventListener("DOMContentLoaded", () => {

    if (document.querySelector("#loginForm")) {
        localStorage.removeItem("usuario");
    }

    // --------------- LOGIN (PRUEBA DE CONEXIÓN) ------------------
    // 💡 Escuchamos el 'click' en el botón para que coincida con tu login.html (type="button")
    const loginButton = document.querySelector("#loginButton");

    if (loginButton) {
        loginButton.addEventListener("click", async (e) => {
            
            const email = document.querySelector("#logEmail").value.trim();
            const pass = document.querySelector("#logPassword").value.trim();

            if (!validarEmail(email)) return alert("El correo no es válido");
            if (pass.length < 6) return alert("La contraseña es demasiado corta");

            try {
                // 💡 1. SALTAMOS la autenticación de Firebase para probar solo la conexión al backend
                console.log("Simulando autenticación exitosa. Forzando conexión a Spring...");
                const token = "TOKEN_DE_PRUEBA_EXITOSA_123456"; // Token FALSO

                // 2. 💡 LLAMADA DE PRUEBA al BACKEND DE SPRING BOOT (URL ABSOLUTA para Render)
                const backendUrl = 'https://demoscanwatter.onrender.com/api/login/firebase';
                
                const response = await fetch(backendUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ token: token })
                });

                if (!response.ok) {
                    const errorMsg = await response.text();
                    // Spring Boot DEBERÍA responder con 401/403 porque el token es falso.
                    // Si llegamos aquí, ¡la conexión fue exitosa!
                    console.log("Conexión con el servidor exitosa (Spring Server rechazó el token falso).");
                    
                    // 💡 Mostramos una alerta que confirma que el fetch SÍ se ejecutó.
                    alert(`Conexión HTTP OK. Falló la creación de sesión (TOKEN FALSO). Mensaje de Spring: ${errorMsg.substring(0, 50)}...`); 
                    
                    // Detenemos la ejecución aquí, no intentamos redirigir.
                    return; 
                }

                // 4. Éxito (solo llegaremos aquí si el token falso fue aceptado, lo cual es casi imposible)
                localStorage.setItem("usuario", email);
                window.location.href = "/dashboard"; 

            } catch (err) {
                console.error("Error crítico en la conexión fetch:", err);
                // Si este alert aparece, el problema es que la URL no se pudo resolver (Failed to fetch)
                alert("ERROR CRÍTICO: La conexión al servidor falló. Verifica la URL de Render."); 
            }
        });
    }

    // --------------- REGISTRO y LOGOUT (Mantenemos la estructura) ----------------
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        // ... Lógica de registro ...
    }
});

// =================== CERRAR SESIÓN ===================
function logout() {
    localStorage.removeItem("usuario");
    firebase.auth().signOut().then(() => {
        window.location.href = "login.html"; 
    });
}