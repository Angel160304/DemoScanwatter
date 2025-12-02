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
            e.preventDefault(); // Siempre buena práctica en formularios
            
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
                    // 🛑 CRÍTICO: Eliminar 'credentials: include' y enviar el token en el BODY
                    body: JSON.stringify({ idToken: token }), // Asegúrate de que coincida con LoginRequest en Java
                });

                if (response.status === 403) {
                     // 403 persistente significa que el cambio a Stateless no funcionó, pero al menos no es de sesión.
                    throw new Error("El servidor rechazó la solicitud (403). Confirma que la configuración de Spring Boot está en modo STATLESS.");
                }
                
                if (!response.ok) {
                    const errorJson = await response.json();
                    const errorMsg = errorJson.message || `Error status: ${response.status}`;
                    throw new Error(`Fallo al validar token en el servidor: ${errorMsg}`);
                }
                
                // 4. ✅ Éxito: Almacenar el token y redirigir
                localStorage.setItem("usuario", email);
                localStorage.setItem("firebaseIdToken", token); // 🛑 CRÍTICO: Guardar el token para futuras peticiones
                
                window.location.href = "/dashboard"; 

            } catch (err) {
                console.error("Error de autenticación o sesión:", err);
                alert(`Error al iniciar sesión: ${err.message || "Credenciales incorrectas o usuario no existe."}`); 
            }
        });
    }

    // --------------- REGISTRO y LOGOUT (Aseguramos que logout también borre el token) ----------------
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        // ... Lógica de registro ...
    }
});

// =================== CERRAR SESIÓN ===================
function logout() {
    localStorage.removeItem("usuario");
    localStorage.removeItem("firebaseIdToken"); // 🛑 CRÍTICO: Borrar el token al cerrar sesión
    firebase.auth().signOut().then(() => {
        window.location.href = "login.html"; 
    });
}