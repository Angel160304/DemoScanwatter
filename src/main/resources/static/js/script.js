// === Scroll suave ===
function scrollToSection(id){
  document.getElementById(id).scrollIntoView({behavior:'smooth'});
}

// === Gráfica Lineal ===
const ctxLine = document.getElementById('lineChart').getContext('2d');
const lineChart = new Chart(ctxLine, {
    type:'line',
    data:{
        labels:[], // Se llenarán dinámicamente
        datasets:[{
            label:'Litros consumidos (L/min)',
            data:[],
            borderColor:'#00bfff',
            backgroundColor:'rgba(0,191,255,0.2)',
            fill:true,
            tension:0.4,
            borderWidth:3
        }]
    },
    options:{
        responsive:true,
        plugins:{legend:{labels:{color:'#fff'}}},
        scales:{
            y:{ticks:{color:'#fff'}},
            x:{ticks:{color:'#fff'}}
        }
    }
});

// === Gráfica de Barras ===
const ctxBar = document.getElementById('barChart').getContext('2d');
const barChart = new Chart(ctxBar, {
    type:'bar',
    data:{
        labels:[],
        datasets:[{
            label:'Litros consumidos (L/min)',
            data:[],
            backgroundColor:'rgba(0,191,255,0.6)',
            borderColor:'#00bfff',
            borderWidth:2
        }]
    },
    options:{
        responsive:true,
        plugins:{legend:{labels:{color:'#fff'}}},
        scales:{
            y:{ticks:{color:'#fff'}},
            x:{ticks:{color:'#fff'}}
        }
    }
});

// === Nueva función para actualizar los datos desde tu backend ===
async function actualizarGraficas() {
  try {
    // 👉 Cambia localhost si estás usando otro puerto/IP
    const res = await fetch('http://localhost:8080/api/flujo/datos');
    const data = await res.json();

    // Si no hay datos, salimos
    if (!data || data.length === 0) return;

    // Crear etiquetas con la hora de cada medición
    const labels = data.map(d => {
      const fecha = new Date(d.timestamp);
      return fecha.toLocaleTimeString();
    });

    const valores = data.map(d => d.valor);

    // === Actualizamos los datos de las gráficas ===
    lineChart.data.labels = labels;
    lineChart.data.datasets[0].data = valores;
    lineChart.update();

    barChart.data.labels = labels;
    barChart.data.datasets[0].data = valores;
    barChart.update();

  } catch (error) {
    console.error('❌ Error al actualizar gráficas:', error);
  }
}

// === Llamar la función cada 3 segundos ===
setInterval(actualizarGraficas, 3000);

// === Cargar datos al abrir la página ===
actualizarGraficas();