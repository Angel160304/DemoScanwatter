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
        await firebase.auth().createUserWithEmailAndPassword(email, pass);

        // 🔹 COMENTADO: ya no enviamos token al backend
        /*
        const token = await firebase.auth().currentUser.getIdToken();
        const response = await fetch(`${API_URL}/register`, {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": `Bearer ${token}`
          },
          body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(pass)}`
        });
        const data = await response.text();
        if (data.startsWith("Error")) { alert(data); }
        */

        window.location.href = "/login.html"; // Redirige al login
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
        await firebase.auth().signInWithEmailAndPassword(email, pass);

        // 🔹 COMENTADO: ya no validamos token con backend
        /*
        const token = await firebase.auth().currentUser.getIdToken();
        const response = await fetch(`${API_URL}/verify-token`, {
          method: "POST",
          headers: {"Content-Type": "application/json"},
          body: JSON.stringify({ idToken: token })
        });
        const data = await response.json();
        if (!response.ok) { alert("Error al validar la sesión"); return; }
        localStorage.setItem("userToken", token);
        */

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
