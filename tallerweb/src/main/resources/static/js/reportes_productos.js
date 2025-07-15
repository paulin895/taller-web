const token = localStorage.getItem("token");
const tallerId = localStorage.getItem("tallerId");
const filtroCategoria = document.getElementById("filtroCategoria");

if (!token || !tallerId) {
  alert("No estás autenticado.");
  window.location.href = "login.html";
}

let productosOriginales = [];

async function cargarCategorias() {
  try {
    const res = await fetch(`http://localhost:8080/api/categorias/taller/${tallerId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const categorias = await res.json();

    categorias.forEach((cat) => {
      const opt = document.createElement("option");
      opt.value = cat.id;
      opt.textContent = cat.nombre;
      filtroCategoria.appendChild(opt);
    });
  } catch (error) {
    console.error("Error cargando categorías:", error);
  }
}

async function cargarProductos() {
  try {
    const res = await fetch(`http://localhost:8080/api/productos/taller/${tallerId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    productosOriginales = await res.json();
    aplicarFiltro(); // mostrar todos por defecto
  } catch (error) {
    console.error("Error cargando productos:", error);
  }
}

function aplicarFiltro() {
  const categoriaSeleccionada = filtroCategoria.value;

  const filtrados = categoriaSeleccionada
    ? productosOriginales.filter(p => p.categoria?.id?.toString() === categoriaSeleccionada)
    : productosOriginales;

  renderTabla(filtrados);
  renderEstadisticas(filtrados);
}

function renderTabla(data) {
  const tabla = $("#tablaReporteProductos");

  if ($.fn.DataTable.isDataTable(tabla)) {
    tabla.DataTable().clear().destroy();
  }

  const tbody = tabla.find("tbody");
  tbody.empty();

  data.forEach((p) => {
    const tr = `
      <tr>
        <td>${p.categoria?.nombre || ""}</td>
        <td>${p.nombre}</td>
        <td>${p.codigo || ""}</td>
        <td>${p.cantidad}</td>
        <td>${parseFloat(p.precioCompra).toFixed(2)}</td>
        <td>${parseFloat(p.precioVenta).toFixed(2)}</td>
        <td>${p.observaciones || ""}</td>
      </tr>
    `;
    tbody.append(tr);
  });

  tabla.DataTable({
    scrollX: true,
    dom: "Bfrtip",
    buttons: ["copy", "excel", "pdf", "print"],
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json",
    },
  });

  // Contador de productos
  document.getElementById("contadorProductos").textContent =
    `🧾 Mostrando ${data.length} producto${data.length === 1 ? "" : "s"}`;
}

function renderEstadisticas(data) {
  const totalProductos = data.length;
  const stockTotal = data.reduce((acc, p) => acc + (p.cantidad || 0), 0);
  const valorTotal = data.reduce((acc, p) => acc + ((p.cantidad || 0) * (p.precioVenta || 0)), 0);

  document.getElementById("total-productos").textContent = totalProductos;
  document.getElementById("stock-total").textContent = stockTotal;
  document.getElementById("valor-total").textContent = `$ ${valorTotal.toFixed(2)}`;
}

// Eventos
filtroCategoria.addEventListener("change", aplicarFiltro);

// Inicialización
cargarCategorias();
cargarProductos();
