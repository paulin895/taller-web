const token = localStorage.getItem("token");
const tallerId = localStorage.getItem("tallerId");

const tableView = document.getElementById("tableView");
const cardsView = document.getElementById("cardsView");
const toggleBtn = document.getElementById("toggleViewBtn");
const buscadorInput = document.getElementById("buscadorCard");

const form = document.getElementById("formProducto");
const mensaje = document.getElementById("mensaje");
const categoriaSelect = document.getElementById("categoriaSelect");
const productoIdInput = document.getElementById("productoId");

const btnAgregarProducto = document.getElementById("btnAgregarProducto");
const modalProducto = document.getElementById("modalProducto");
const cerrarModal = document.getElementById("cerrarModal");

if (!token) {
  alert("No estás logueado");
  window.location.href = "login.html";
}

// Cambiar entre vista cards y tabla
toggleBtn.addEventListener("click", () => {
  cardsView.classList.toggle("hidden");
  tableView.classList.toggle("hidden");

  const estaEnVistaCards = !cardsView.classList.contains("hidden");

  // Mostrar/ocultar solo el input, no el botón
  buscadorInput.style.display = estaEnVistaCards ? "block" : "none";
  buscadorInput.value = ""; // opcional: limpiar

  toggleBtn.textContent = estaEnVistaCards ? "Ver como Tabla" : "Ver como Tarjetas";
});

// Buscar en cards
buscadorInput.addEventListener("input", function () {
  const query = this.value.toLowerCase();
  const cards = document.querySelectorAll("#cardsView .product-card");

  cards.forEach((card) => {
    const texto = card.textContent.toLowerCase();
    card.style.display = texto.includes(query) ? "block" : "none";
  });
});

function mostrarMensaje(msg, color = "red") {
  mensaje.textContent = msg;
  mensaje.style.color = color;
  if (msg) {
    setTimeout(() => (mensaje.textContent = ""), 4000);
  }
}

async function cargarCategorias() {
  try {
    const res = await fetch(`http://localhost:8080/api/categorias/taller/${tallerId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const categorias = await res.json();
    categoriaSelect.innerHTML = `<option value="">-- Seleccione categoría --</option>`;
    categorias.forEach((c) => {
      const opt = document.createElement("option");
      opt.value = c.id;
      opt.textContent = c.nombre;
      categoriaSelect.appendChild(opt);
    });
  } catch {
    mostrarMensaje("Error cargando categorías", "red");
  }
}

async function listarProductos() {
  try {
    const res = await fetch(`http://localhost:8080/api/productos/taller/${tallerId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const productos = await res.json();
    renderCards(productos);
    renderTable(productos);
  } catch {
    mostrarMensaje("Error al cargar productos");
  }
}

function renderCards(productos) {
  cardsView.innerHTML = "";
  productos.forEach((p) => {
    const card = document.createElement("div");
    card.className = "product-card";
    card.innerHTML = `
      <h3>${p.nombre}</h3>
      <p><strong>Categoría:</strong> ${p.categoria?.nombre || "-"}</p>
      <p><strong>Código:</strong> ${p.codigo || "-"}</p>
      <p><strong>Cantidad:</strong> ${p.cantidad}</p>
      <p><strong>Precio Compra:</strong> $${parseFloat(p.precioCompra).toFixed(2)}</p>
      <p><strong>Precio Venta:</strong> $${parseFloat(p.precioVenta).toFixed(2)}</p>
      <div class="card-actions">
        <button class="btn-editar" data-id="${p.id}"><i class="fas fa-edit"></i></button>
        <button class="btn-eliminar" data-id="${p.id}"><i class="fas fa-trash"></i></button>
      </div>
    `;
    cardsView.appendChild(card);
  });

  document.querySelectorAll(".btn-editar").forEach((btn) => {
    btn.onclick = () => editarProducto(btn.dataset.id);
  });

  document.querySelectorAll(".btn-eliminar").forEach((btn) => {
    btn.onclick = () => eliminarProducto(btn.dataset.id);
  });
}

function renderTable(productos) {
  const tbody = document.querySelector("#tablaProductos tbody");
  tbody.innerHTML = "";
  productos.forEach((p) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${p.categoria?.nombre || ""}</td>
      <td>${p.nombre}</td>
      <td>${p.codigo || ""}</td>
      <td>${p.cantidad}</td>
      <td>${parseFloat(p.precioCompra).toFixed(2)}</td>
      <td>${parseFloat(p.precioVenta).toFixed(2)}</td>
      <td>${p.observaciones || ""}</td>
      <td>
        <button class="btn-editar" data-id="${p.id}"><i class="fas fa-edit"></i></button>
        <button class="btn-eliminar" data-id="${p.id}"><i class="fas fa-trash"></i></button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  if ($.fn.DataTable.isDataTable("#tablaProductos")) {
    $("#tablaProductos").DataTable().clear().destroy();
  }

  $("#tablaProductos").DataTable({
    scrollX: true,
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json",
    },
  });

  $("#tablaProductos tbody").on("click", ".btn-editar", function () {
    const id = $(this).data("id");
    editarProducto(id);
  });

  $("#tablaProductos tbody").on("click", ".btn-eliminar", function () {
    const id = $(this).data("id");
    eliminarProducto(id);
  });
}

async function editarProducto(id) {
  try {
    const res = await fetch(`http://localhost:8080/api/productos/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const p = await res.json();
    categoriaSelect.value = p.categoria?.id || "";
    document.getElementById("nombre").value = p.nombre;
    document.getElementById("codigo").value = p.codigo || "";
    document.getElementById("cantidad").value = p.cantidad;
    document.getElementById("precioCompra").value = p.precioCompra;
    document.getElementById("precioVenta").value = p.precioVenta;
    document.getElementById("observaciones").value = p.observaciones || "";
    productoIdInput.value = p.id;
    document.getElementById("form-title").textContent = "Editar Producto";
    modalProducto.classList.remove("hidden");
    mostrarMensaje("", "");
  } catch {
    mostrarMensaje("Error al cargar producto para editar", "red");
  }
}

function eliminarProducto(id) {
  if (!confirm("¿Eliminar producto?")) return;
  fetch(`http://localhost:8080/api/productos/${id}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  }).then((res) => {
    if (res.ok) {
      mostrarMensaje("Producto eliminado", "green");
      listarProductos();
    } else {
      mostrarMensaje("Error al eliminar", "red");
    }
  });
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  if (!categoriaSelect.value) {
    mostrarMensaje("Seleccione una categoría", "red");
    return;
  }

  const data = {
    categoria: { id: Number(categoriaSelect.value) },
    taller: { id: Number(tallerId) },
    nombre: document.getElementById("nombre").value.trim(),
    codigo: document.getElementById("codigo").value.trim() || null,
    cantidad: Number(document.getElementById("cantidad").value),
    precioCompra: parseFloat(document.getElementById("precioCompra").value),
    precioVenta: parseFloat(document.getElementById("precioVenta").value),
    observaciones: document.getElementById("observaciones").value.trim() || null,
  };

  const productoId = productoIdInput.value;
  const url = productoId
    ? `http://localhost:8080/api/productos/${productoId}`
    : "http://localhost:8080/api/productos";
  const method = productoId ? "PUT" : "POST";

  try {
    const res = await fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });

    if (res.ok) {
      form.reset();
      productoIdInput.value = "";
      document.getElementById("form-title").textContent = "Agregar Producto";
      mostrarMensaje("Guardado correctamente", "green");
      listarProductos();
    } else {
      mostrarMensaje("Error al guardar", "red");
    }
  } catch {
    mostrarMensaje("Error en la conexión", "red");
  }
});

btnAgregarProducto.addEventListener("click", () => {
  document.getElementById("formProducto").reset();
  productoIdInput.value = "";
  document.getElementById("form-title").textContent = "Agregar Producto";
  modalProducto.classList.remove("hidden");
});

cerrarModal.addEventListener("click", () => {
  modalProducto.classList.add("hidden");
});

window.addEventListener("click", (e) => {
  if (e.target === modalProducto) {
    modalProducto.classList.add("hidden");
  }
});

// Inicialización
cargarCategorias();
listarProductos();
