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
        // Asegúrate de limpiar también el token JWT al inicio de la página de login
        localStorage.removeItem("usuario");
        localStorage.removeItem("firebaseIdToken"); // Limpiar token al cargar login
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
                // 1. 🔑 Autenticación REAL con Firebase
                const userCredential = await firebase.auth().signInWithEmailAndPassword(email, pass);
                const user = userCredential.user;

                // 2. Obtener el ID Token (JWT)
                const token = await user.getIdToken();

                // 3. 🌐 Llamada al BACKEND (Validación Stateless)
                const backendUrl = 'https://demoscanwatter.onrender.com/api/login/firebase';
                
                const response = await fetch(backendUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    // ✅ CORREGIDO: Usamos 'idToken' para que coincida con el backend de Spring Boot
                    body: JSON.stringify({ idToken: token }), 
                });

                // --- 🛑 LÓGICA DE MANEJO DE ERRORES ROBUSTA (JSON o Texto) ---
                if (!response.ok) {
                    let errorMsg;
                    try {
                        // Intenta leer el cuerpo como JSON (para errores 400/500 estructurados)
                        const errorJson = await response.json();
                        errorMsg = errorJson.message || errorJson.error || `Error status: ${response.status}`;
                    } catch (e) {
                        // Si falla la lectura de JSON (ej: respuesta 401/403 con texto plano o errores de Cloudflare)
                        const errorText = await response.text();
                        errorMsg = errorText || `Error del servidor con estado ${response.status}`;

                        // Manejo de errores específicos del backend
                        if (errorMsg.includes("Token ID is required")) {
                            errorMsg = "Fallo de validación: El token no fue enviado correctamente.";
                        }
                    }

                    // Lanzar el error para que sea capturado por el 'catch'
                    throw new Error(`Fallo al validar token: ${errorMsg}`);
                }
                // --- FIN DE LA LÓGICA DE MANEJO DE ERRORES ---
                
                // 4. ✅ Éxito: Almacenar el token y redirigir
                localStorage.setItem("usuario", email);
                localStorage.setItem("firebaseIdToken", token); // Guardar el token para futuras peticiones
                
                window.location.href = "/dashboard"; 

            } catch (err) {
                console.error("Error de autenticación o sesión:", err);
                alert(`Error al iniciar sesión: ${err.message || "Credenciales incorrectas o usuario no existe."}`); 
            }
        });
    }

    // --------------- REGISTRO y LOGOUT ----------------
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        // ... Lógica de registro ...
    }
});

// =================== CERRAR SESIÓN ===================
function logout() {
    localStorage.removeItem("usuario");
    localStorage.removeItem("firebaseIdToken"); // Borrar el token al cerrar sesión
    firebase.auth().signOut().then(() => {
        window.location.href = "login.html"; 
    });
}