// === Scroll suave ===
function scrollToSection(id) {
  const element = document.getElementById(id);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  } else {
    console.warn(`⚠ No se encontró la sección con id "${id}".`);
  }
}

// === Verificaciones iniciales ===
if (typeof Chart === "undefined") {
  console.error("❌ ERROR: Chart.js no está cargado. Revisa la etiqueta <script> en el HTML.");
}

const canvasLine = document.getElementById('lineChart');
const canvasBar = document.getElementById('barChart');

if (!canvasLine || !canvasBar) {
  console.error("❌ ERROR: No se encontraron los elementos canvas para las gráficas.");
}

// === Inicializar gráficas ===
let lineChart, barChart;

function inicializarGraficas() {
  if (!canvasLine || !canvasBar) return;

  const ctxLine = canvasLine.getContext('2d');
  const ctxBar = canvasBar.getContext('2d');

  lineChart = new Chart(ctxLine, {
    type: 'line',
    data: { labels: [], datasets: [{
        label: 'Litros consumidos (L/min)',
        data: [],
        borderColor: '#00bfff',
        backgroundColor: 'rgba(0,191,255,0.2)',
        fill: true,
        tension: 0.4,
        borderWidth: 3
    }]},
    options: {
      responsive: true,
      plugins: { legend: { labels: { color: '#fff' } } },
      scales: {
        y: { ticks: { color: '#fff' } },
        x: { ticks: { color: '#fff' } }
      }
    }
  });

  barChart = new Chart(ctxBar, {
    type: 'bar',
    data: { labels: [], datasets: [{
        label: 'Litros consumidos (L/min)',
        data: [],
        backgroundColor: 'rgba(0,191,255,0.6)',
        borderColor: '#00bfff',
        borderWidth: 2
    }]},
    options: {
      responsive: true,
      plugins: { legend: { labels: { color: '#fff' } } },
      scales: {
        y: { ticks: { color: '#fff' } },
        x: { ticks: { color: '#fff' } }
      }
    }
  });
}

// === Actualizar datos desde backend ===
async function actualizarGraficas() {
  if (!lineChart || !barChart) return;

  try {
    const res = await fetch('https://demoscanwatter.onrender.com/api/flujo/datos');

    if (!res.ok) {
      console.warn(`⚠ Backend respondió con estado ${res.status}`);
      return;
    }

    const data = await res.json();
    if (!Array.isArray(data) || data.length === 0) {
      console.warn("⚠ No hay datos para mostrar.");
      return;
    }

    const labels = data.map(d => new Date(d.timestamp).toLocaleTimeString());
    const valores = data.map(d => d.valor || 0);

    lineChart.data.labels = labels;
    lineChart.data.datasets[0].data = valores;
    lineChart.update();

    barChart.data.labels = labels;
    barChart.data.datasets[0].data = valores;
    barChart.update();

  } catch (error) {
    console.error("❌ Error al actualizar gráficas:", error);
  }
}

// === Carga inicial y actualización periódica ===
document.addEventListener("DOMContentLoaded", () => {
  inicializarGraficas();
  actualizarGraficas();
  setInterval(actualizarGraficas, 3000);
});
