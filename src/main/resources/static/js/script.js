// === Scroll suave ===
function scrollToSection(id) {
  document.getElementById(id).scrollIntoView({ behavior: 'smooth' });
}

// Verificar si Chart.js está cargado
if (typeof Chart === "undefined") {
  console.error("❌ ERROR: Chart.js no está cargado. Revisa CDN en el HTML.");
}

// Verificación de elementos canvas
const canvasLine = document.getElementById('lineChart');
const canvasBar = document.getElementById('barChart');

if (!canvasLine || !canvasBar) {
  console.error("❌ ERROR: No se encontraron los canvas de las gráficas.");
}

// === Gráfica Lineal ===
const lineChart = new Chart(canvasLine.getContext('2d'), {
  type: 'line',
  data: {
    labels: [], // Se llenarán dinámicamente
    datasets: [{
      label: 'Litros consumidos (L/min)',
      data: [],
      borderColor: '#00bfff',
      backgroundColor: 'rgba(0,191,255,0.2)',
      fill: true,
      tension: 0.4,
      borderWidth: 3
    }]
  },
  options: {
    responsive: true,
    plugins: {
      legend: { labels: { color: '#fff' } }
    },
    scales: {
      y: { ticks: { color: '#fff' } },
      x: { ticks: { color: '#fff' } }
    }
  }
});

// === Gráfica de Barras ===
const barChart = new Chart(canvasBar.getContext('2d'), {
  type: 'bar',
  data: {
    labels: [],
    datasets: [{
      label: 'Litros consumidos (L/min)',
      data: [],
      backgroundColor: 'rgba(0,191,255,0.6)',
      borderColor: '#00bfff',
      borderWidth: 2
    }]
  },
  options: {
    responsive: true,
    plugins: {
      legend: { labels: { color: '#fff' } }
    },
    scales: {
      y: { ticks: { color: '#fff' } },
      x: { ticks: { color: '#fff' } }
    }
  }
});

// === Función para actualizar los datos desde backend ===
async function actualizarGraficas() {
  try {
    const res = await fetch('https://demoscanwatter.onrender.com/api/flujo/datos');

    if (!res.ok) {
      throw new Error(`Error HTTP: ${res.status}`);
    }

    const data = await res.json();

    if (!Array.isArray(data) || data.length === 0) {
      console.warn("⚠ No hay datos para mostrar.");
      return;
    }

    // Crear etiquetas tipo HH:mm:ss
    const labels = data.map(d => {
      const fecha = new Date(d.timestamp);
      return fecha.toLocaleTimeString();
    });

    const valores = data.map(d => d.valor);

    // Actualizar datos
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

// === Actualización periódica ===
setInterval(actualizarGraficas, 3000);

// === Cargar datos al abrir la página ===
document.addEventListener("DOMContentLoaded", () => {
  actualizarGraficas();
});
