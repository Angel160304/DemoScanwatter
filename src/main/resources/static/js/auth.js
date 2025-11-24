// ====== CONFIGURACIÓN DE BACKEND ======
const API_URL = "https://demoscanwatter.onrender.com/api/auth";

// ===== VALIDACIÓN DE EMAIL Y PASSWORD (Mantenido) =====
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

document.addEventListener("DOMContentLoaded", () => {
    
  // ===== REGISTRO (Mantenido) =====
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
        // 🔥 Registro en Firebase
        await firebase.auth().createUserWithEmailAndPassword(email, pass);
        const token = await firebase.auth().currentUser.getIdToken();

        // 📡 Registro en backend con token (Se mantiene tu lógica original)
        const response = await fetch(`${API_URL}/register`, {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": `Bearer ${token}`
          },
          body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(pass)}`
        });

        const data = await response.text();

        if (data.startsWith("Error")) {
          alert(data);
        } else {
          window.location.href = "/login"; // redirige al login
        }
      } catch (err) {
        console.error("Error en registro:", err);
        alert("Error al registrar");
      }
    });
  }

  // ===== LOGIN (MODIFICADO) =====
  const loginForm = document.querySelector("#loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const email = document.querySelector("#logEmail").value.trim();
      const pass = document.querySelector("#logPassword").value.trim();

      if (!validarEmail(email)) return alert("El correo no es válido");
      if (pass.length < 6) return alert("La contraseña es demasiado corta");

      try {
        // 🔥 1. Login en Firebase
        await firebase.auth().signInWithEmailAndPassword(email, pass);
        const token = await firebase.auth().currentUser.getIdToken();

        // 📡 2. Validar token en backend y crear la sesión de Spring Security
        //    *** Importante: Usamos /verify-token y enviamos el token en JSON ***
        const response = await fetch(`${API_URL}/verify-token`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json", // Cambiado a JSON
          },
          body: JSON.stringify({ idToken: token }) // Enviamos el token en el cuerpo
        });
        
        const data = await response.json(); // Leemos como JSON para errores o éxito

        if (!response.ok) { // Si el servidor retorna un error (401, 500, etc.)
            const errorMessage = data.message || "Error al validar la sesión en el servidor.";
            alert(`Error de validación: ${errorMessage}`);
            console.error("Server Token Error:", data);
            return; 
        } 
        
        // 3. Login exitoso y sesión de Spring establecida
        localStorage.setItem("usuario", email);
        localStorage.setItem("userToken", token); 
        window.location.href = "/index"; // Redirige a la ruta protegida
        
      } catch (err) {
        console.error("Firebase Login Error:", err);
        alert("Error al autenticar, verifica tus credenciales.");
      }
    });
  }
});

// ===== FUNCIÓN GLOBAL PARA CERRAR SESIÓN (Mantenida) =====
function logout() {
  localStorage.removeItem("usuario");
  localStorage.removeItem("userToken");
  firebase.auth().signOut().then(() => {
    window.location.href = "/login"; // redirige al login
  });
}