// Archivo: auth.js

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

// Función para enviar token al backend y obtener respuesta
async function sendTokenToBackend(token) {
    const backendUrl = '/api/login/firebase'; // Usar ruta relativa
    
    // El backend necesita el token en el cuerpo para validarlo
    const response = await fetch(backendUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // NO enviamos el token aquí, el backend lo recibirá del cuerpo
        },
        body: JSON.stringify({ idToken: token }), 
    });

    // 🛑 CRÍTICO: Leer el cuerpo de la respuesta UNA SOLA VEZ
    const responseText = await response.text();

    if (!response.ok) {
        let errorMsg = `Error status: ${response.status}`;
        
        try {
            // Intenta parsear el JSON
            const errorJson = JSON.parse(responseText);
            errorMsg = errorJson.error || errorJson.message || `Error status: ${response.status}`;
        } catch (e) {
            // Si no es JSON, usa el texto plano y adivina el error 401
            if (response.status === 401) {
                errorMsg = "Token de Firebase Inválido o Expirado. Por favor, inicia sesión de nuevo.";
            } else {
                 errorMsg = responseText.substring(0, 100) + "..."; 
            }
        }
        throw new Error(`Fallo al crear sesión: ${errorMsg}`);
    }
    
    // Si la respuesta es OK (200), asumimos éxito.
    // Opcionalmente, puedes retornar el JSON parseado aquí si el backend devuelve datos.
    return JSON.parse(responseText); 
}


// =================== EVENTOS ===================
document.addEventListener("DOMContentLoaded", () => {

    if (document.querySelector("#loginForm")) {
        localStorage.removeItem("usuario");
        localStorage.removeItem("firebaseIdToken");
    }

    // --------------- LOGIN ------------------
    const loginButton = document.querySelector("#loginButton");

    if (loginButton) {
        loginButton.addEventListener("click", async (e) => {
            e.preventDefault(); 
            
            const email = document.querySelector("#logEmail").value.trim();
            const pass = document.querySelector("#logPassword").value.trim();

            if (!validarEmail(email)) return alert("El correo no es válido");
            if (pass.length < 6) return alert("La contraseña es demasiado corta");

            try {
                // 1. Autenticación REAL con Firebase
                const userCredential = await firebase.auth().signInWithEmailAndPassword(email, pass);
                const user = userCredential.user;

                // 2. Obtener el ID Token (JWT)
                const token = await user.getIdToken();

                // 3. Llamada al BACKEND (Validación y Creación de Sesión)
                // Usamos la nueva función para encapsular la lógica de fetch
                const backendResponse = await sendTokenToBackend(token);
                
                // 4. Éxito: Almacenar el token y redirigir
                localStorage.setItem("usuario", email);
                localStorage.setItem("firebaseIdToken", token); 
                
                // 🛑 CRÍTICO: El token debe ir en el encabezado para /dashboard
                // Lo inyectamos en la siguiente petición.
                
                window.location.href = "/dashboard"; // Redirección al endpoint del controlador

            } catch (err) {
                console.error("Error de autenticación o sesión:", err);
                alert(`Error al iniciar sesión: ${err.message || "Credenciales incorrectas o usuario no existe."}`); 
            }
        });
    }

    // --------------- REGISTRO ------------------
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        registroForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const email = document.getElementById("regEmail").value.trim();
            const password = document.getElementById("regPassword").value.trim();
            const confirmPass = document.getElementById("regConfirm").value.trim();

            if (password !== confirmPass) return alert("Las contraseñas no coinciden");
            if (!validarEmail(email)) return alert("El correo no es válido");
            if (password.length < 6) return alert("La contraseña debe tener al menos 6 caracteres.");

            try {
                // Crear el usuario en Firebase (Client-side)
                await firebase.auth().createUserWithEmailAndPassword(email, password);
                
                alert("¡Registro exitoso! Por favor, inicia sesión.");
                window.location.href = "login.html";

            } catch (error) {
                alert("Error al registrar usuario: " + error.message);
                console.error("Error de registro:", error);
            }
        });
    }
    
    // --------------- INYECCIÓN DE TOKEN PARA RUTAS PROTEGIDAS ------------------
    // Este código se ejecuta en el dashboard o en cualquier página protegida.
    const token = localStorage.getItem("firebaseIdToken");
    if (token) {
        // Esta es una solución simple para inyectar el token en cada XHR/fetch request.
        // Una solución completa usaría un interceptor de fetch/axios.
        // Para solicitudes AJAX manuales (que no son login/registro) DEBES incluir:
        // 'Authorization': 'Bearer ' + token
        
        // EJEMPLO: Asegurar que el token se envía en solicitudes protegidas:
        
        // window.fetch = new Proxy(window.fetch, {
        //     apply: function(target, that, args) {
        //         if (args[1] && args[1].headers) {
        //             args[1].headers['Authorization'] = 'Bearer ' + token;
        //         } else if (args[1]) {
        //             args[1].headers = { 'Authorization': 'Bearer ' + token };
        //         } else {
        //             args[1] = { headers: { 'Authorization': 'Bearer ' + token } };
        //         }
        //         return Reflect.apply(target, that, args);
        //     }
        // });
        
        // Como alternativa más simple:
        // Si tu código en dashboard.html usa fetch o XMLHttpRequest, 
        // ¡asegúrate de incluir el encabezado 'Authorization'!
    } else if (window.location.pathname !== '/login.html' && window.location.pathname !== '/registro.html' && window.location.pathname !== '/') {
        // Si no hay token y no estamos en una página pública, redirigir al login
        // window.location.href = "login.html";
    }
});

// =================== CERRAR SESIÓN ===================
function logout() {
    localStorage.removeItem("usuario");
    localStorage.removeItem("firebaseIdToken");
    firebase.auth().signOut().then(() => {
        window.location.href = "login.html"; 
    });
}