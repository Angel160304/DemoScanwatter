// ====== CONFIGURACIÓN DE BACKEND (ya no se usa) ======
// const API_URL = "https://demoscanwatter.onrender.com/api/auth";

// ===== VALIDACIÓN DE EMAIL Y PASSWORD =====
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
  // ===== REGISTRO =====
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
        // Registro directo con Firebase
        await firebase.auth().createUserWithEmailAndPassword(email, pass);

        alert("Usuario registrado correctamente");
        window.location.href = "/login.html"; // Redirige al login

      } catch (err) {
        console.error("Error en registro:", err);
        alert("Error al registrar: " + err.message);
      }
    });
  }

  // ===== LOGIN =====
  const loginForm = document.querySelector("#loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const email = document.querySelector("#logEmail").value.trim();
      const pass = document.querySelector("#logPassword").value.trim();

      if (!validarEmail(email)) return alert("El correo no es válido");
      if (pass.length < 6) return alert("La contraseña es demasiado corta");

      try {
        // Login directo con Firebase
        await firebase.auth().signInWithEmailAndPassword(email, pass);

        // Guardamos solo el correo del usuario en localStorage
        localStorage.setItem("usuario", email);

        // REDIRECCIÓN SOLO DESPUÉS DE LOGIN
        window.location.href = "/dashboard";

      } catch (err) {
        console.error("Firebase Login Error:", err);
        alert("Error al autenticar, verifica tus credenciales.");
      }
    });
  }
});

// ===== CERRAR SESIÓN =====
function logout() {
  localStorage.removeItem("usuario");
  firebase.auth().signOut().then(() => {
    window.location.href = "/login.html";
  });
}
