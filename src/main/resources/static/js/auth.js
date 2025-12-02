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

                // 3. Llamada al BACKEND (Validación Stateless)
                const backendUrl = 'https://demoscanwatter.onrender.com/api/login/firebase';
                
                const response = await fetch(backendUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ idToken: token }), 
                });

                // --- 🛑 LÓGICA DE MANEJO DE ERRORES CORREGIDA (Lectura Única) ---
                if (!response.ok) {
                    // CRÍTICO: Leer el cuerpo SOLO UNA VEZ.
                    const responseText = await response.text();
                    let errorMsg = `Error status: ${response.status}`;
                    
                    try {
                        // Intenta parsear el JSON que ahora el servidor debe devolver
                        const errorJson = JSON.parse(responseText);
                        errorMsg = errorJson.error || errorJson.message || `Error status: ${response.status}`;
                    } catch (e) {
                        // Si no es JSON (es texto plano o HTML), usa el texto plano.
                        errorMsg = responseText.substring(0, 100) + "..."; // Recortar texto
                        if (response.status === 401) {
                            errorMsg = "Token de Firebase Inválido o Expirado. Por favor, inicia sesión de nuevo.";
                        }
                    }
                    // Lanza el error capturado
                    throw new Error(`Fallo al crear sesión: ${errorMsg}`);
                }
                // --- FIN DE LA LÓGICA DE MANEJO DE ERRORES ---
                
                // 4. Éxito: Almacenar el token y redirigir
                localStorage.setItem("usuario", email);
                localStorage.setItem("firebaseIdToken", token); 
                
                window.location.href = "/dashboard"; 

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
                // El error 403 (Clave API) se captura aquí
                alert("Error al registrar usuario: " + error.message);
                console.error("Error de registro:", error);
            }
        });
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