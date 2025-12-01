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
    }

    // --------------- LOGIN ------------------
    const loginButton = document.querySelector("#loginButton");

    if (loginButton) {
        loginButton.addEventListener("click", async (e) => {
            
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

                // 3. 🌐 Llamada al BACKEND DE SPRING BOOT (URL ABSOLUTA para Render)
                const backendUrl = 'https://demoscanwatter.onrender.com/api/login/firebase';
                
                const response = await fetch(backendUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ token: token }),
                    credentials: 'include' // ⬅️ CRÍTICO: Asegura que la cookie JSESSIONID se guarde
                });

                if (!response.ok) {
                    const errorMsg = await response.text();
                    throw new Error(`Fallo al crear sesión en el servidor: ${errorMsg}`);
                }

                // 4. Éxito: Redirigir al Dashboard
                localStorage.setItem("usuario", email);
                window.location.href = "/dashboard"; 

            } catch (err) {
                console.error("Error de autenticación o sesión:", err);
                alert(`Error al iniciar sesión: ${err.message || "Credenciales incorrectas o usuario no existe."}`); 
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