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

function validarPassword(password) {
    if (password.length < 8) return alert("La contraseña debe tener al menos 8 caracteres.");
    if (!/[a-z]/.test(password)) return alert("Debe incluir al menos una letra minúscula.");
    if (!/[A-Z]/.test(password)) return alert("Debe incluir al menos una letra mayúscula.");
    if (!/[0-9]/.test(password)) return alert("Debe incluir al menos un número.");
    if (!/[^A-Za-z0-9]/.test(password)) return alert("Debe incluir un carácter especial.");
    return true;
}

// =================== EVENTOS ===================
document.addEventListener("DOMContentLoaded", () => {

    // Si estás en la página de login, asegúrate de borrar cualquier sesión anterior.
    if (document.querySelector("#loginForm")) {
        localStorage.removeItem("usuario");
    }

    // --------------- REGISTRO ----------------
    const registroForm = document.querySelector("#registroForm");
    if (registroForm) {
        registroForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const email = document.querySelector("#regEmail").value.trim();
            const pass = document.querySelector("#regPassword").value.trim();
            const confirmPass = document.querySelector("#regConfirm").value.trim();

            if (!validarEmail(email)) return alert("El correo no es válido");
            if (!validarPassword(pass)) return;
            if (pass !== confirmPass) return alert("Las contraseñas no coinciden");

            try {
                await firebase.auth().createUserWithEmailAndPassword(email, pass);
                alert("Usuario registrado correctamente");
                window.location.href = "login.html";
            } catch (err) {
                console.error("Error en registro:", err);
                alert("Error al registrar: " + err.message);
            }
        });
    }

   // --------------- LOGIN (VERSIÓN CON CLICK) ------------------
const loginButton = document.querySelector(".submit"); // O document.querySelector("#loginButton")
if (loginButton) {
    loginButton.addEventListener("click", async (e) => {
        // e.preventDefault() YA NO ES NECESARIO si el botón es type="button"
        
        const email = document.querySelector("#logEmail").value.trim();
        const pass = document.querySelector("#logPassword").value.trim();

        if (!validarEmail(email)) return alert("El correo no es válido");
        if (pass.length < 6) return alert("La contraseña es demasiado corta");

        try {
            // 💡 1. SALTAMOS FIREBASE
            console.log("Simulando autenticación exitosa. Enviando token a Spring...");
            const token = "TOKEN_DE_PRUEBA_EXITOSA"; 

            // 💡 2. FORZAMOS EL ENVÍO DEL TOKEN AL BACKEND DE SPRING BOOT
            const response = await fetch('/api/login/firebase', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ token: token })
            });

            if (!response.ok) {
                const errorMsg = await response.text();
                throw new Error(`Fallo de conexión con Spring: ${errorMsg}`);
            }

            // 3. Éxito de conexión 
            localStorage.setItem("usuario", email);
            window.location.href = "/dashboard"; 

        } catch (err) {
            console.error("Error en la prueba de conexión:", err);
            alert("Error de conexión con el servidor: " + err.message);
            return;
        }
    });

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