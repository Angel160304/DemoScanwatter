// ====== CONFIGURACIÓN DE BACKEND ======
const API_URL = "https://demoscanwatter.onrender.com/api/auth";
const auth = firebase.auth();
const database = firebase.database(); // Firebase 8

// ===== VALIDACIÓN DE EMAIL Y PASSWORD =====
function validarEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validarPassword(password) {
  if (password.length < 8) return alert("La contraseña debe tener al menos 8 caracteres.");
  if (!/[a-z]/.test(password)) return alert("Debe incluir al menos una letra minúscula.");
  if (!/[A-Z]/.test(password)) return alert("Debe incluir al menos una letra mayúscula.");
  if (!/[0-9]/.test(password)) return alert("Debe incluir un número.");
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
        await auth.createUserWithEmailAndPassword(email, pass);
        const token = await auth.currentUser.getIdToken();

        const response = await fetch(`${API_URL}/register`, {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": `Bearer ${token}`
          },
          body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(pass)}`
        });

        const data = await response.text();
        if (data.startsWith("Error")) alert(data);
        else window.location.href = "/login";
      } catch (err) {
        console.error("Error en registro:", err);
        alert("Error al registrar");
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
        await auth.signInWithEmailAndPassword(email, pass);
        const token = await auth.currentUser.getIdToken();
        const response = await fetch(`${API_URL}/verify-token`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ idToken: token })
        });

        const data = await response.json();
        if (!response.ok) {
          const errorMessage = data.message || "Error al validar la sesión en el servidor.";
          alert(`Error de validación: ${errorMessage}`);
          return;
        }

        // Guardamos usuario, token y rol
        localStorage.setItem("usuario", email);
        localStorage.setItem("userToken", token);

        // Obtenemos el rol desde el token
        const role = await auth.currentUser.getIdTokenResult();
        localStorage.setItem("userRole", role.claims.role || "USER");

        window.location.href = "/index";
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
  localStorage.removeItem("userToken");
  localStorage.removeItem("userRole");
  auth.signOut().then(() => window.location.href = "/login");
}
