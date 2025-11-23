// ====== CONFIGURACIÓN DE BACKEND ======
const API_URL = "https://demoscanwatter.onrender.com/api/auth";

// ===== VALIDACIÓN DE EMAIL =====
function validarEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// ===== VALIDACIÓN DE CONTRASEÑA =====
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
    registroForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const email = document.querySelector("#regEmail").value.trim();
      const pass = document.querySelector("#regPassword").value.trim();
      const confirmPass = document.querySelector("#regConfirm").value.trim();

      if (!validarEmail(email)) return alert("El correo no es válido");
      if (!validarPassword(pass)) return;
      if (pass !== confirmPass) return alert("Las contraseñas no coinciden");

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
          // Registro también en Firebase
          firebase.auth().createUserWithEmailAndPassword(email, pass)
            .then(() => window.location.href = "login.html")
            .catch(err => console.error("Firebase Register Error:", err));
        }
      })
      .catch(err => alert("Error al registrar: " + err));
    });
  }

  // ===== LOGIN =====
  const loginForm = document.querySelector("#loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const email = document.querySelector("#logEmail").value.trim();
      const pass = document.querySelector("#logPassword").value.trim();

      if (!validarEmail(email)) return alert("El correo no es válido");
      if (pass.length < 6) return alert("La contraseña es demasiado corta");

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
          // Guarda sesión local
          localStorage.setItem("usuario", email);

          // 🔥 Inicia sesión en Firebase
          firebase.auth().signInWithEmailAndPassword(email, pass)
            .then(() => window.location.href = "index.html")
            .catch(err => {
              console.error("Firebase Login Error:", err);
              alert("Error al autenticar con Firebase.");
            });
        }
      })
      .catch(err => alert("Error al iniciar sesión: " + err));
    });
  }
});

// ===== FUNCIÓN GLOBAL PARA CERRAR SESIÓN =====
function logout() {
  localStorage.removeItem("usuario");
  firebase.auth().signOut().then(() => {
    window.location.href = "login.html";
  });
}
