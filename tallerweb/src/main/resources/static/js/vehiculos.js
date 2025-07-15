const token = localStorage.getItem("token");
const tallerId = localStorage.getItem("tallerId");
const clientesCache = [];

(function () {
  if (!token) {
    alert("🔒 No estás autenticado. Serás redirigido al login.");
    window.location.href = "login.html";
    return;
  }

  if (!tallerId) {
    alert("⚠️ No se encontró taller asociado.");
    return;
  }

  const form = document.getElementById("form-vehiculo");
  const mensaje = document.getElementById("mensaje");
  const clienteBusqueda = document.getElementById("clienteBusqueda");
  const clienteIdInput = document.getElementById("clienteId");
  const vehiculoIdInput = document.getElementById("vehiculoId");
  const datalist = document.getElementById("listaClientes");
  let tablaVehiculos;

  // Cargar clientes
  async function cargarClientes() {
    try {
      const res = await fetch(`http://localhost:8080/api/clientes/taller/${tallerId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      const clientes = await res.json();
      clientesCache.length = 0;
      clientes.forEach(c => clientesCache.push(c));

      datalist.innerHTML = "";
      clientes.forEach(c => {
        const option = document.createElement("option");
        option.value = c.nombreCompleto;
        datalist.appendChild(option);
      });
    } catch (e) {
      mostrarMensaje("❌ Error al cargar clientes", "red");
    }
  }

  // Validar cliente al tipear
  clienteBusqueda.addEventListener("input", () => {
    const texto = clienteBusqueda.value.trim().toLowerCase();
    const cliente = clientesCache.find(c => c.nombreCompleto.toLowerCase() === texto);
    if (cliente) {
      clienteIdInput.value = cliente.id;
      mostrarMensaje("", "");
    } else {
      clienteIdInput.value = "";
      mostrarMensaje("⚠️ Cliente no encontrado", "orange");
    }
  });

  // Mostrar mensaje
  function mostrarMensaje(texto, color) {
    mensaje.textContent = texto;
    mensaje.style.color = color;
  }

  // Listar vehículos
  async function listarVehiculos() {
    try {
      const res = await fetch(`http://localhost:8080/api/vehiculos/taller/${tallerId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      const vehiculos = await res.json();
      const tbody = document.querySelector("#tablaVehiculos tbody");
      tbody.innerHTML = "";

      vehiculos.forEach(v => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${v.nombreCliente || "Sin cliente"}</td>
          <td>${v.marca}</td>
          <td>${v.modelo}</td>
          <td>${v.matricula}</td>
          <td>${v.anio}</td>
          <td>${v.color || ""}</td>
          <td>${v.observaciones || ""}</td>
          <td>
            <button class="btn-accion btn-editar" data-id="${v.id}"><i class="fas fa-edit"></i></button>
            <button class="btn-accion btn-eliminar" data-id="${v.id}"><i class="fas fa-trash-alt"></i></button>
          </td>
        `;
        tbody.appendChild(tr);
      });

      asignarEventos();

      if (tablaVehiculos) tablaVehiculos.destroy();

      tablaVehiculos = $("#tablaVehiculos").DataTable({
        responsive: true,
        pageLength: 5,
        language: {
          url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
        }
      });

    } catch (e) {
      mostrarMensaje("❌ Error al listar vehículos", "red");
    }
  }

  function asignarEventos() {
    document.querySelectorAll(".btn-editar").forEach(btn => {
      btn.addEventListener("click", () => editarVehiculo(btn.dataset.id));
    });

    document.querySelectorAll(".btn-eliminar").forEach(btn => {
      btn.addEventListener("click", () => eliminarVehiculo(btn.dataset.id));
    });
  }

  async function editarVehiculo(id) {
    try {
      const res = await fetch(`http://localhost:8080/api/vehiculos/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      const v = await res.json();
      clienteBusqueda.value = v.cliente?.nombreCompleto || "";
      clienteIdInput.value = v.cliente?.id || "";
      document.getElementById("marca").value = v.marca;
      document.getElementById("modelo").value = v.modelo;
      document.getElementById("matricula").value = v.matricula;
      document.getElementById("anio").value = v.anio;
      document.getElementById("color").value = v.color || "";
      document.getElementById("observaciones").value = v.observaciones || "";
      vehiculoIdInput.value = v.id;

      mostrarMensaje("", "");
      modal.classList.remove("hidden"); // <<--- ¡Esto es lo que te faltaba!
    } catch (e) {
      mostrarMensaje("❌ Error al cargar vehículo para editar", "red");
    }
  }

  async function eliminarVehiculo(id) {
    if (!confirm("¿Seguro que deseas eliminar este vehículo?")) return;
    try {
      const res = await fetch(`http://localhost:8080/api/vehiculos/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error();
      mostrarMensaje("✅ Vehículo eliminado correctamente", "green");
      listarVehiculos();
    } catch (e) {
      mostrarMensaje("❌ Error al eliminar vehículo", "red");
    }
  }

  form.addEventListener("submit", async e => {
    e.preventDefault();

    const data = {
      cliente: { id: Number(clienteIdInput.value) },
      taller: { id: Number(tallerId) },
      marca: document.getElementById("marca").value.trim(),
      modelo: document.getElementById("modelo").value.trim(),
      matricula: document.getElementById("matricula").value.trim(),
      anio: parseInt(document.getElementById("anio").value.trim()),
      color: document.getElementById("color").value.trim(),
      observaciones: document.getElementById("observaciones").value.trim()
    };

    if (!data.cliente.id) return mostrarMensaje("⚠️ Seleccioná un cliente válido", "red");
    if (!data.marca || !data.modelo || !data.matricula || isNaN(data.anio)) {
      return mostrarMensaje("⚠️ Completá todos los campos obligatorios", "red");
    }

    const id = vehiculoIdInput.value;
    const url = `http://localhost:8080/api/vehiculos${id ? `/${id}` : ""}`;
    const method = id ? "PUT" : "POST";

    try {
      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(data)
      });

      if (!res.ok) throw new Error();
      mostrarMensaje(id ? "✅ Vehículo actualizado" : "✅ Vehículo guardado", "green");
      form.reset();
      clienteIdInput.value = "";
      vehiculoIdInput.value = "";
      listarVehiculos();
    } catch (e) {
      mostrarMensaje("❌ Error al guardar vehículo", "red");
    }
  });

  // Inicial
  cargarClientes().then(listarVehiculos);

})();

document.addEventListener("DOMContentLoaded", () => {
  const btnAgregar = document.getElementById("btn-agregar");
  const modal = document.getElementById("modal-vehiculo");
  const cerrarModal = document.getElementById("cerrar-modal");
  const form = document.getElementById("form-vehiculo");
  const clienteIdInput = document.getElementById("clienteId");
  const vehiculoIdInput = document.getElementById("vehiculoId");

  btnAgregar.addEventListener("click", () => {
    document.getElementById("form-title").textContent = "Agregar Vehículo";
    form.reset();
    clienteIdInput.value = "";
    vehiculoIdInput.value = "";
    modal.classList.remove("hidden");
  });

  cerrarModal.addEventListener("click", () => {
    modal.classList.add("hidden");
  });

  window.addEventListener("click", e => {
    if (e.target === modal) {
      modal.classList.add("hidden");
    }
  });
});
