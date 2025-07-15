const token = localStorage.getItem("token");
    const tallerId = localStorage.getItem("tallerId");
    const form = document.getElementById("formCategoria");
    const mensaje = document.getElementById("mensaje");
    const categoriaIdInput = document.getElementById("categoriaId");
    let tabla;

    if (!token) { alert("No estás logueado"); window.location.href = "login.html"; }

    async function listarCategorias() {
      try {
        const res = await fetch(`http://localhost:8080/api/categorias/taller/${tallerId}`, { headers: { Authorization: `Bearer ${token}` } });
        const categorias = await res.json();
        const tbody = document.querySelector("#tablaCategorias tbody");
        tbody.innerHTML = "";
        categorias.forEach(c => {
          const tr = document.createElement("tr");
          tr.innerHTML = `<td>${c.nombre}</td><td>${c.observaciones || ""}</td><td><button class='btn-accion btn-editar' data-id='${c.id}'><i class='fas fa-edit'></i></button><button class='btn-accion btn-eliminar' data-id='${c.id}'><i class='fas fa-trash'></i></button></td>`;
          tbody.appendChild(tr);
        });
        if (tabla) tabla.destroy();
        tabla = $("#tablaCategorias").DataTable();
        asignarEventosBotones();
      } catch (e) { mostrarMensaje("Error al listar categorías", "red"); }
    }

    function asignarEventosBotones() {
      document.querySelectorAll(".btn-editar").forEach(btn => btn.onclick = () => editarCategoria(btn.dataset.id));
      document.querySelectorAll(".btn-eliminar").forEach(btn => btn.onclick = () => eliminarCategoria(btn.dataset.id));
    }

    async function editarCategoria(id) {
      const res = await fetch(`http://localhost:8080/api/categorias/${id}`, { headers: { Authorization: `Bearer ${token}` } });
      const c = await res.json();
      document.getElementById("nombre").value = c.nombre;
      document.getElementById("observaciones").value = c.observaciones || "";
      categoriaIdInput.value = c.id;
      document.getElementById("form-title").textContent = "Editar Categoría";
    }
function eliminarCategoria(id) {
  if (!confirm("¿Eliminar categoría?")) return;

  fetch(`http://localhost:8080/api/categorias/${id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
  .then(async res => {
    if (res.ok) {
      listarCategorias();
      mostrarMensaje("Categoría eliminada correctamente", "green");
    } else {
      const error = await res.text();
      mostrarMensaje(error || "No se pudo eliminar la categoría", "red");
    }
  })
  .catch(err => {
    console.error("Error de red al eliminar:", err);
    mostrarMensaje("Error de conexión al eliminar la categoría", "red");
  });
}

form.addEventListener("submit", async e => {
  e.preventDefault();

  const data = {
    nombre: document.getElementById("nombre").value,
    observaciones: document.getElementById("observaciones").value || null,
    taller: { id: Number(tallerId) }
  };

  const categoriaId = categoriaIdInput.value;
  const url = categoriaId
    ? `http://localhost:8080/api/categorias/${categoriaId}`
    : "http://localhost:8080/api/categorias";

  const method = categoriaId ? "PUT" : "POST";

  try {
    const res = await fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(data)
    });

    if (res.ok) {
      form.reset();
      categoriaIdInput.value = "";
      listarCategorias();
      mostrarMensaje("Guardado correctamente", "green");
    } else {
      const error = await res.text();
      mostrarMensaje(error || "Error al guardar la categoría", "red");
    }
  } catch (err) {
    console.error("Error de red:", err);
    mostrarMensaje("Error de conexión al guardar la categoría", "red");
  }
});

    function mostrarMensaje(msg, color) {
      mensaje.textContent = msg;
      mensaje.style.color = color;
    }

    listarCategorias();
