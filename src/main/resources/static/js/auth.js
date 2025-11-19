// ====== CONFIGURACIÓN DE BACKEND ======
const API_URL = "https://demoscanwatter.onrender.com/api/auth";

// ===== VALIDACIÓN DE EMAIL =====
function validarEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

// ===== VALIDACIÓN DE CONTRASEÑA =====
function validarPassword(password) {
  if (password.length < 8) {
    alert("La contraseña debe tener al menos 8 caracteres.");
    return false;
  }
  if (!/[a-z]/.test(password)) {
    alert("La contraseña debe incluir al menos una letra minúscula.");
    return false;
  }
  if (!/[A-Z]/.test(password)) {
    alert("La contraseña debe incluir al menos una letra mayúscula.");
    return false;
  }
  if (!/[0-9]/.test(password)) {
    alert("La contraseña debe incluir al menos un número.");
    return false;
  }
  if (!/[^A-Za-z0-9]/.test(password)) {
    alert("La contraseña debe incluir al menos un carácter especial.");
    return false;
  }
  return true;
}

// ===== FUNCIONES DE REGISTRO Y LOGIN =====
document.addEventListener("DOMContentLoaded", () => {

  // ===== REGISTROoo =====
  const registroForm = document.querySelector("#registroForm");
  if (registroForm) {
    registroForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const email = document.querySelector("#regEmail").value.trim();
      const pass = document.querySelector("#regPassword").value.trim();
      const confirmPass = document.querySelector("#regConfirm").value.trim();

      if (!validarEmail(email)) {
        alert("El correo no es válido");
        return;
      }

      if (!validarPassword(pass)) return;

      if (pass !== confirmPass) {
        alert("Las contraseñas no coinciden");
        return;
      }

      // ===== LLAMADA AL BACKEND PARA REGISTRO =====
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
          window.location.href = "login.html";
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

      if (!validarEmail(email)) {
        alert("El correo no es válido");
        return;
      }

      if (pass.length < 6) {
        alert("La contraseña es demasiado corta");
        return;
      }

      // ===== LLAMADA AL BACKEND PARA LOGIN =====
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
          window.location.href = "index.html";
        }
      })
      .catch(err => alert("Error al iniciar sesión: " + err));
    });
  }
});
