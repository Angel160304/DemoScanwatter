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

// Implementar aquí el resto de validaciones como validarPassword, si las necesitas.
// function validarPassword(password) { ... }

// =================== EVENTOS ===================
document.addEventListener("DOMContentLoaded", () => {

    // Si estás en la página de login, asegúrate de borrar cualquier sesión anterior.
    if (document.querySelector("#loginForm")) {
        localStorage.removeItem("usuario");
    }

    // --------------- LOGIN (CRÍTICO) ------------------
    // 💡 TU HTML USA <button type="button" id="loginButton">, por lo tanto, escuchamos el 'click'
    const loginButton = document.querySelector("#loginButton");

    if (loginButton) {
        loginButton.addEventListener("click", async (e) => {
            // No es necesario e.preventDefault() ya que el botón es type="button"
            
            const email = document.querySelector("#logEmail").value.trim();
            const pass = document.querySelector("#logPassword").value.trim();

            if (!validarEmail(email)) return alert("El correo no es válido");
            if (pass.length < 6) return alert("La contraseña es demasiado corta"); // Validación mínima

            try {
                // 1. Iniciar sesión con Firebase
                const userCredential = await firebase.auth().signInWithEmailAndPassword(email, pass);
                const user = userCredential.user;

                // 2. Obtener el ID Token (JWT)
                const token = await user.getIdToken();

                // 3. 💡 CORRECCIÓN PARA RENDER: Usar la URL ABSOLUTA para evitar "Failed to fetch"
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
                    // Si Spring Boot rechaza el token (ej: usuario no autorizado), falla aquí.
                    throw new Error(`Fallo al crear sesión en el servidor: ${errorMsg}`);
                }

                // 4. Éxito: Crear sesión de Spring Security y redirigir
                localStorage.setItem("usuario", email);
                window.location.href = "/dashboard"; 

            } catch (err) {
                console.error("Error de autenticación o sesión:", err);
                // Muestra el mensaje de error de Firebase (ej: wrong-password, user-not-found)
                alert("Error al autenticar: verifica tus credenciales."); 
            }
        });
    }

    // --------------- REGISTRO (Si está en el mismo archivo) ----------------
    // Debes incluir la lógica de registro aquí si es que la tienes
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        // ... Lógica de registro ...
    }
});

// =================== CERRAR SESIÓN ===================
function logout() {
    localStorage.removeItem("usuario");
    firebase.auth().signOut().then(() => {
        // Redirigir a la página de login después de cerrar la sesión de Firebase
        window.location.href = "login.html"; 
    });
}