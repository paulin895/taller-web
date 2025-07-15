// Accesibles para todo el archivo
const token = localStorage.getItem("token");
const tallerId = localStorage.getItem("tallerId");
const apiUrl = "http://localhost:8080/api/clientes";

(function () {
  if (!token) {
    alert("No estás autenticado. Serás redirigido al login.");
    window.location.href = "login.html";
    return;
  }

  function obtenerRolesDesdeToken(token) {
    try {
      const payloadBase64 = token.split('.')[1];
      const base64 = payloadBase64.replace(/-/g, '+').replace(/_/g, '/');
      const payloadJson = decodeURIComponent(atob(base64).split('').map(c =>
        `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`
      ).join(''));
      const payload = JSON.parse(payloadJson);
      return payload.roles || [];
    } catch (e) {
      console.error("Error decodificando token:", e);
      return [];
    }
  }

  const roles = obtenerRolesDesdeToken(token);
  if (!roles.includes("ROLE_ADMIN")) {
    alert("No tenés permiso para acceder a esta página.");
    window.location.href = "login.html";
    return;
  }

  if (!tallerId) {
    document.getElementById("mensaje-error-taller").style.display = "block";
  } else {
    document.getElementById("form-cliente").style.display = "block";

  }

  document.getElementById("cerrar-sesion")?.addEventListener("click", function (e) {
    e.preventDefault();
    localStorage.removeItem("token");
    localStorage.removeItem("tallerId");
    window.location.href = "login.html";
  });

  let tablaDataTable = null;

  function listarClientes() {
    fetch(`${apiUrl}/taller/${tallerId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(data => {
        if (tablaDataTable) {
          tablaDataTable.clear().draw();
          tablaDataTable.rows.add(data).draw();
        } else {
          inicializarDataTable(data);
        }
      })
      .catch(() => mostrarMensaje("❌ Error al cargar clientes.", "error"));
  }

  function inicializarDataTable(data) {
    tablaDataTable = $('#tabla-clientes').DataTable({
      data: data,
      columns: [
        { data: 'nombreCompleto' },
        { data: 'ci' },
        { data: 'telefono' },
        { data: 'correo' },
        { data: 'direccion' },
        { data: 'observaciones' },
        {
          data: null,
          render: function (data, type, row) {
            return `
              <button class="edit" onclick="editarCliente(${row.id})"><i class="fas fa-edit"></i></button>
              <button class="delete" onclick="eliminarCliente(${row.id})"><i class="fas fa-trash-alt"></i></button>
            `;
          }
        }
      ],
      language: {
        url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
      },
      pageLength: 8,
      dom: '<"top"f>rtip'
    });
  }

  function mostrarMensaje(mensaje, tipo) {
    const div = document.createElement("div");
    div.textContent = mensaje;
    div.className = "message " + (tipo === "error" ? "error" : "success");
    document.querySelector("section.content-wrapper").prepend(div);
    setTimeout(() => div.remove(), 3000);
  }

  const form = document.getElementById("form-cliente");
  form.addEventListener("submit", e => {
    e.preventDefault();

    const clienteId = document.getElementById("cliente-id").value;
    const nuevoCliente = {
      nombreCompleto: document.getElementById("nombreCompleto").value.trim(),
      ci: document.getElementById("ci").value.trim(),
      telefono: document.getElementById("telefono").value.trim(),
      correo: document.getElementById("correo").value.trim(),
      direccion: document.getElementById("direccion").value.trim(),
      observaciones: document.getElementById("observaciones").value.trim(),
      taller: { id: Number(tallerId) }
    };

    const metodo = clienteId ? "PUT" : "POST";
    const url = clienteId ? `${apiUrl}/${clienteId}` : apiUrl;

    fetch(url, {
      method: metodo,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(nuevoCliente)
    })
      .then(res => {
        if (res.ok) {
          mostrarMensaje(clienteId ? "✅ Cliente actualizado." : "✅ Cliente guardado.", "success");
          form.reset();
          document.getElementById("cliente-id").value = "";
          listarClientes();
        } else {
          mostrarMensaje("⚠️ Error al guardar cliente.", "error");
        }
      })
      .catch(() => mostrarMensaje("❌ Error de conexión.", "error"));
  });

  window.editarCliente = function (id) {
    const tablaModal = $('#tabla-clientes-modal').DataTable();
    const cliente = tablaModal.data().toArray().find(c => c.id === id);
    if (!cliente) {
      console.warn("Cliente no encontrado con id:", id);
      return;
    }

    document.getElementById("nombreCompleto").value = cliente.nombreCompleto || "";
    document.getElementById("ci").value = cliente.ci || "";
    document.getElementById("telefono").value = cliente.telefono || "";
    document.getElementById("correo").value = cliente.correo || "";
    document.getElementById("direccion").value = cliente.direccion || "";
    document.getElementById("observaciones").value = cliente.observaciones || "";
    document.getElementById("cliente-id").value = cliente.id;

    // Mostrar el formulario
    document.getElementById("form-cliente").style.display = "block";

    // Cerrar el modal
    document.getElementById("modal-clientes").style.display = "none";

    // Subir al formulario
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  window.eliminarCliente = function (id) {
    if (!confirm("¿Eliminar cliente?")) return;

    fetch(`${apiUrl}/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        if (res.ok) {
          mostrarMensaje("🗑️ Cliente eliminado.", "success");
          listarClientes();
        } else {
          mostrarMensaje("⚠️ Error al eliminar.", "error");
        }
      })
      .catch(() => mostrarMensaje("❌ Error de conexión.", "error"));
  };

  document.getElementById("btn-buscar-clientes")?.addEventListener("click", () => {
    document.querySelector(".tabla-container").style.display = "block";
    listarClientes();
    setTimeout(() => {
      document.querySelector(".tabla-container").scrollIntoView({ behavior: 'smooth' });
    }, 300);
  });

  document.getElementById("menu-toggle")?.addEventListener("click", () => {
    document.querySelector(".sidebar").classList.toggle("open");
  });
})();

// === MODAL de clientes ===
const modal = document.getElementById("modal-clientes");
const btnAbrirModal = document.getElementById("btn-ver-modal-clientes");
const btnCerrarModal = document.getElementById("cerrar-modal");

btnAbrirModal?.addEventListener("click", () => {
  modal.style.display = "flex";
  cargarTablaEnModal();
});

btnCerrarModal?.addEventListener("click", () => {
  modal.style.display = "none";
});

window.addEventListener("click", (e) => {
  if (e.target === modal) {
    modal.style.display = "none";
  }
});

function cargarTablaEnModal() {
  fetch(`${apiUrl}/taller/${tallerId}`, {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      if ($.fn.DataTable.isDataTable('#tabla-clientes-modal')) {
        $('#tabla-clientes-modal').DataTable().clear().rows.add(data).draw();
      } else {
        $('#tabla-clientes-modal').DataTable({
          data: data,
          columns: [
            { data: 'nombreCompleto' },
            { data: 'ci' },
            { data: 'telefono' },
            { data: 'correo' },
            { data: 'direccion' },
            { data: 'observaciones' },
            {
              data: null,
              render: function (data, type, row) {
                return `
                  <button class="edit" onclick="editarCliente(${row.id})"><i class="fas fa-edit"></i></button>
                  <button class="delete" onclick="eliminarCliente(${row.id})"><i class="fas fa-trash-alt"></i></button>
                `;
              }
            }
          ],
          language: {
            url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
          },
          pageLength: 10
        });
      }
    });
}

//mostrar usuario logeado
function mostrarUsuarioLogueado() {
  const token = localStorage.getItem("token");
  if (!token) return;

  fetch("http://localhost:8080/api/usuarios/yo", {
    headers: {
      Authorization: "Bearer " + token
    }
  })
    .then(res => {
      if (!res.ok) throw new Error("No se pudo obtener el usuario");
      return res.json();
    })
    .then(usuario => {
      const span = document.getElementById("usuario-logueado");
      if (span) span.textContent = "Hola, " + usuario.nombre;
    })
    .catch(err => {
      console.error("Error obteniendo usuario:", err);
    });
}

document.addEventListener("DOMContentLoaded", mostrarUsuarioLogueado);


document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.querySelector(".sidebar");
  const menuToggle = document.getElementById("menu-toggle");
  const overlay = document.getElementById("overlay");

  menuToggle.addEventListener("click", () => {
    sidebar.classList.toggle("open");
    overlay.classList.toggle("active");

    menuToggle.innerHTML = sidebar.classList.contains("open")
      ? '<i class="fas fa-times"></i>'
      : '<i class="fas fa-bars"></i>';
  });

  overlay.addEventListener("click", () => {
    sidebar.classList.remove("open");
    overlay.classList.remove("active");
    menuToggle.innerHTML = '<i class="fas fa-bars"></i>';
  });

  // Cargar nombre del usuario logueado
  const token = localStorage.getItem("token");
  if (token) {
    fetch("http://localhost:8080/api/usuarios/yo", {
      headers: {
        Authorization: "Bearer " + token
      }
    })
      .then(res => res.ok ? res.json() : Promise.reject())
      .then(user => {
        const span = document.getElementById("usuario-logueado");
        if (span) span.textContent = "Hola, " + user.nombre;
      })
      .catch(() => {
        const span = document.getElementById("usuario-logueado");
        if (span) span.textContent = "Usuario desconocido";
      });
  }
});
