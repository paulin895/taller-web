// reportes_ordenes.js

const token = localStorage.getItem("token");
const tallerId = localStorage.getItem("tallerId");

const graficoOrdenesCanvas = document.getElementById("graficoOrdenes");
const graficoIngresosCanvas = document.getElementById("graficoIngresos");

let graficoOrdenes, graficoIngresos;

async function obtenerOrdenes() {
  try {
    const res = await fetch("http://localhost:8080/api/ordenes", {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("Error al obtener órdenes");
    const data = await res.json();
    return data.filter((o) => o.tallerId == tallerId);
  } catch (error) {
    console.error("❌ Error cargando órdenes:", error);
    return [];
  }
}

function formatearMes(fechaStr) {
  const fecha = new Date(fechaStr);
  const año = fecha.getFullYear();
  const mes = String(fecha.getMonth() + 1).padStart(2, "0");
  return `${año}-${mes}`;
}

function aplicarFiltros(ordenes) {
  const estado = document.getElementById("filtroEstado").value;
  const desde = document.getElementById("filtroDesde").value;
  const hasta = document.getElementById("filtroHasta").value;

  return ordenes.filter((o) => {
    const fecha = o.fechaEntrada;
    return (
      (!estado || o.estado === estado) &&
      (!desde || fecha >= desde) &&
      (!hasta || fecha <= hasta)
    );
  });
}

function actualizarTarjetas(stats) {
  document.getElementById("total-ordenes").textContent = stats.total;
  document.getElementById("ordenes-pendientes").textContent = stats.pendientes;
  document.getElementById("ordenes-proceso").textContent = stats.enProceso;
  document.getElementById("ordenes-cerradas").textContent = stats.cerradas;
  document.getElementById("total-ingresos").textContent = `$ ${stats.ingresos.toFixed(2)}`;
}

function calcularEstadisticas(ordenes) {
  const stats = {
    total: ordenes.length,
    pendientes: 0,
    enProceso: 0,
    cerradas: 0,
    ingresos: 0,
    porMes: {},
  };

  ordenes.forEach((o) => {
    const estado = o.estado;
    if (estado === "PENDIENTE") stats.pendientes++;
    else if (estado === "EN_PROCESO") stats.enProceso++;
    else if (estado === "CERRADA") {
      stats.cerradas++;
      stats.ingresos += o.precio || 0;
    }

    const mes = formatearMes(o.fechaEntrada);
    if (!stats.porMes[mes]) stats.porMes[mes] = { cantidad: 0, ingresos: 0 };
    stats.porMes[mes].cantidad++;
    if (estado === "CERRADA") stats.porMes[mes].ingresos += o.precio || 0;
  });

  return stats;
}

function renderizarGraficos(stats) {
  const labels = Object.keys(stats.porMes).sort();
  const cantidadData = labels.map((mes) => stats.porMes[mes].cantidad);
  const ingresosData = labels.map((mes) => stats.porMes[mes].ingresos);

  if (graficoOrdenes) graficoOrdenes.destroy();
  if (graficoIngresos) graficoIngresos.destroy();

  graficoOrdenes = new Chart(graficoOrdenesCanvas, {
    type: "bar",
    data: {
      labels,
      datasets: [{
        label: "Órdenes por mes",
        data: cantidadData,
        backgroundColor: "rgba(54, 162, 235, 0.6)",
      }],
    },
  });

  graficoIngresos = new Chart(graficoIngresosCanvas, {
    type: "line",
    data: {
      labels,
      datasets: [{
        label: "Ingresos por mes",
        data: ingresosData,
        fill: true,
        borderColor: "#28a745",
        backgroundColor: "rgba(40, 167, 69, 0.2)",
      }],
    },
  });
}

function cargarTabla(ordenes) {
  const tabla = $("#tablaReporteOrdenes").DataTable();
  tabla.clear();

  ordenes.forEach((o) => {
    tabla.row.add([
      o.clienteNombreCompleto || "",
      o.vehiculoDescripcion || "",
      o.mecanicoNombre || "",
      o.fechaEntrada || "",
      o.fechaSalida || "",
      o.estado || "",
      o.precio != null ? `$${o.precio.toFixed(2)}` : "",
      o.descripcion || "",
    ]);
  });

  tabla.draw();
}

let ordenesGlobales = [];

async function init() {
  if (!token || !tallerId) return;

  ordenesGlobales = await obtenerOrdenes();
  const ordenesFiltradas = aplicarFiltros(ordenesGlobales);
  const stats = calcularEstadisticas(ordenesFiltradas);

  actualizarTarjetas(stats);
  renderizarGraficos(stats);
  cargarTabla(ordenesFiltradas);
}

document.addEventListener("DOMContentLoaded", () => {
  $("#tablaReporteOrdenes").DataTable({
    dom: 'Bfrtip',
    buttons: ['excelHtml5', 'print'],
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json",
    },
    columns: Array(8).fill(null).map(() => ({ orderable: true })),
  });

  document.getElementById("btnAplicarFiltros").addEventListener("click", () => {
    const ordenesFiltradas = aplicarFiltros(ordenesGlobales);
    const stats = calcularEstadisticas(ordenesFiltradas);

    actualizarTarjetas(stats);
    renderizarGraficos(stats);
    cargarTabla(ordenesFiltradas);
  });

  init();
});
