const chartCanvas = document.getElementById("chart").getContext("2d");
let chart;

document.addEventListener("DOMContentLoaded", () => {
  // Inicializar gráfica vacía
  chart = new Chart(chartCanvas, {
    type: "line",
    data: {
      labels: [],
      datasets: [{
        label: "Litros consumidos (L/min)",
        data: [],
        backgroundColor: "rgba(0,191,255,0.2)",
        borderColor: "#00bfff",
        borderWidth: 2,
        fill: true,
        tension: 0.3
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { labels: { color: "#fff" } } },
      scales: {
        x: { ticks: { color: "#fff" } },
        y: { ticks: { color: "#fff" } }
      }
    }
  });

  // Determinar rol y UID desde localStorage
  const userRole = localStorage.getItem("userRole") || "USER";
  const userUID = localStorage.getItem("userUID") || "";

  // Si no es ADMIN, deshabilitar cualquier acción de modificación
  const dateInput = document.getElementById("datePicker");
  if (userRole !== "ADMIN" || userUID !== "7NdfxdEsU9cSfuHH8cWWthcwGE03") {
    dateInput.setAttribute("readonly", true);
    dateInput.addEventListener("click", () => {
      alert("No tienes permisos para seleccionar una fecha.");
    });
  } else {
    dateInput.removeAttribute("readonly");
  }

  // Cargar datos iniciales
  loadDay();
});

// ===== Función para cargar datos de la fecha seleccionada =====
async function loadDay() {
  const selectedDay = document.getElementById("datePicker").value || getTodayDate();
  const userRole = localStorage.getItem("userRole") || "USER";
  const userUID = localStorage.getItem("userUID") || "";

  // Referencia Firebase
  const ref = firebase.database().ref(`consumo/${selectedDay}`);

  ref.off(); // Quitar listeners previos

  ref.on("value", (snap) => {
    const data = snap.val() || {};

    const labels = Object.keys(data);
    const valores = Object.values(data).map(d => d.valor || 0);

    chart.data.labels = labels;
    chart.data.datasets[0].data = valores;
    chart.update();

    // Si el usuario no es ADMIN, bloquear el borrado
    if (userRole === "ADMIN" && userUID === "7NdfxdEsU9cSfuHH8cWWthcwGE03") {
      enableDeleteButtons(labels, selectedDay);
    }
  });
}

// ===== Función para habilitar botones de borrar solo para admin =====
function enableDeleteButtons(labels, day) {
  labels.forEach(label => {
    const btnId = `delete-${label}`;
    let btn = document.getElementById(btnId);

    if (!btn) {
      btn = document.createElement("button");
      btn.textContent = `Borrar ${label}`;
      btn.id = btnId;
      btn.style.margin = "5px";
      btn.onclick = () => deleteEntry(day, label);
      document.body.appendChild(btn);
    }
  });
}

// ===== Función para borrar un registro específico =====
function deleteEntry(day, key) {
  const userRole = localStorage.getItem("userRole") || "USER";
  const userUID = localStorage.getItem("userUID") || "";

  if (userRole !== "ADMIN" || userUID !== "7NdfxdEsU9cSfuHH8cWWthcwGE03") {
    alert("No tienes permisos para borrar datos.");
    return;
  }

  if (confirm(`¿Seguro que deseas borrar el registro ${key} de ${day}?`)) {
    firebase.database().ref(`consumo/${day}/${key}`).remove()
      .then(() => {
        alert("Registro eliminado correctamente.");
        loadDay(); // Recargar gráfica
      })
      .catch(err => alert("Error al borrar registro: " + err.message));
  }
}

// ===== Obtener fecha de hoy en formato YYYY-MM-DD =====
function getTodayDate() {
  const today = new Date();
  return today.toISOString().split("T")[0];
}
