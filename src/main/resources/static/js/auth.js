// ====== CONFIGURACIÓN BACKEND Y FIREBASE ======
const API_URL = "https://demoscanwatter.onrender.com/api/auth";

// Firebase ya debe estar cargado desde tu HTML
// firebase.initializeApp(firebaseConfig); <-- Esto NO va aquí, ya está en el HTML

// ===== VALIDACIÓN EMAIL =====
function validarEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

// ===== VALIDACIÓN CONTRASEÑA =====
function validarPassword(password) {
  if (password.length < 8) return alert("La contraseña debe tener al menos 8 caracteres.");
  if (!/[a-z]/.test(password)) return alert("Debe incluir al menos una letra minúscula.");
  if (!/[A-Z]/.test(password)) return alert("Debe incluir al menos una letra mayúscula.");
  if (!/[0-9]/.test(password)) return alert("Debe incluir al menos un número.");
  if (!/[^A-Za-z0-9]/.test(password)) return alert("Debe incluir al menos un carácter especial.");
  return true;
}

document.addEventListener("DOMContentLoaded", () => {

  // ===================== REGISTRO =====================
  const registroForm = document.querySelector("#registroForm");
  if (registroForm) {
    registroForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const email = document.querySelector("#regEmail").value.trim();
      const pass = document.querySelector("#regPassword").value.trim();
      const confirmPass = document.querySelector("#regConfirm").value.trim();

      if (!validarEmail(email)) return alert("Correo inválido");
      if (!validarPassword(pass)) return;
      if (pass !== confirmPass) return alert("Las contraseñas no coinciden");

      // 1️⃣ Registrar en BACKEND
      fetch(`${API_URL}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(pass)}`
      })
      .then(res => res.text())
      .then(data => {
        if (data.startsWith("Error")) {
          alert(data);
        } else {
          // 2️⃣ Registrar también en Firebase
          firebase.auth().createUserWithEmailAndPassword(email, pass)
            .then(() => {
              alert("Registro exitoso");
              window.location.href = "login.html";
            })
            .catch(err => alert("Error Firebase registro: " + err.message));
        }
      })
      .catch(err => alert("Error servidor registro: " + err));
    });
  }

  // ===================== LOGIN =====================
  const loginForm = document.querySelector("#loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const email = document.querySelector("#logEmail").value.trim();
      const pass = document.querySelector("#logPassword").value.trim();

      if (!validarEmail(email)) return alert("Correo inválido");
      if (pass.length < 6) return alert("Contraseña demasiado corta");

      // 1️⃣ Login con backend
      fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(pass)}`
      })
      .then(res => res.text())
      .then(data => {
        if (data.startsWith("Error")) {
          alert(data);
        } else {
          // 2️⃣ Login con Firebase
          firebase.auth().signInWithEmailAndPassword(email, pass)
            .then(() => {
              sessionStorage.setItem("loggedIn", "true"); // Guarda sesión
              window.location.href = "index.html";
            })
            .catch(err => alert("Error Firebase login: " + err.message));
        }
      })
      .catch(err => alert("Error servidor login: " + err));
    });
  }
});
