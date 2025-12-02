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
                    
                    // Leer el cuerpo de la respuesta UNA SOLA VEZ como texto
                    const responseText = await response.text();
                    let errorMsg;
                    
                    try {
                        // Intenta parsear el texto leído como JSON
                        const errorJson = JSON.parse(responseText);
                        errorMsg = errorJson.message || errorJson.error || `Error status: ${response.status}`;
                    } catch (e) {
                        // Si falla el parseo, usar el texto plano
                        errorMsg = responseText || `Error del servidor con estado ${response.status}`;
                        
                        // Si el error es el 401 no estructurado de Firebase/Spring
                        if (errorMsg.includes("Invalid to") || response.status === 401) {
                            errorMsg = "Token de Firebase Inválido o Expirado. Por favor, inicia sesión de nuevo.";
                        }
                    }

                    // Lanzar el error
                    throw new Error(`Fallo al validar token: ${errorMsg}`);
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

    // --------------- REGISTRO y LOGOUT ----------------
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        // ... Lógica de registro ...
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