document.addEventListener("DOMContentLoaded", () => {
  (function protegerPagina() {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('No estás logueado. Redirigiendo al login...');
      window.location.href = 'login.html';
      return;
    }

    const roles = obtenerRolesDesdeToken(token);
    const permitido = roles.includes('ROLE_ADMIN') || roles.includes('ROLE_SUPERADMIN');

    if (!permitido) {
      alert('No tenés permiso para acceder a esta página.');
      window.location.href = 'login.html';
    }
  })();

  const token = localStorage.getItem('token');
  const tallerId = localStorage.getItem('tallerId');

  const form = document.getElementById('form-usuario');
  const tabla = new DataTable('#tabla-usuarios', {
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
    }
  });

  const tablaModal = new DataTable('#tabla-usuarios-modal', {
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
    }
  });
  const modal = document.getElementById('modal-usuarios');
  const overlay = document.getElementById('overlay');

  const btnMostrarModal = document.getElementById('btn-ver-modal-usuarios');
  const btnCerrarModal = document.getElementById('cerrar-modal');
  const btnCancelar = document.getElementById('btn-cancelar');
  const btnNuevoUsuario = document.getElementById('btn-nuevo-usuario');

  const rolSelect = document.getElementById('rol');
  const grupoMecanico = document.getElementById('grupo-mecanico');
  const telefono = document.getElementById('telefono');
  const especialidad = document.getElementById('especialidad');
  const observaciones = document.getElementById('observaciones');

  let modoEdicion = false;
  let usuarioId = null;

  btnMostrarModal.addEventListener('click', () => {
    modal.style.display = 'block';
    overlay.classList.add('active');
    cargarUsuariosModal();
  });

  btnCerrarModal.addEventListener('click', () => {
    modal.style.display = 'none';
    overlay.classList.remove('active');
  });



  btnNuevoUsuario.addEventListener('click', () => {
    form.reset();
    form.style.display = 'block';
    modoEdicion = false;
    usuarioId = null;
    grupoMecanico.style.display = 'none';
  });

  rolSelect.addEventListener('change', () => {
    grupoMecanico.style.display = rolSelect.value === 'MECANICO' ? 'block' : 'none';
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const rol = rolSelect.value;
    const estado = document.getElementById('estado').value;

    const data = {
      nombre,
      email,
      rol,
      estado,
      tallerId,
    };

    if (rol === 'MECANICO') {
      data.telefono = telefono.value.trim();
      data.especialidad = especialidad.value.trim();
      data.observaciones = observaciones.value.trim();
    }

    if (!modoEdicion || password) {
      data.password = password;
    }

    const url = `http://localhost:8080/api/usuarios${modoEdicion ? '/' + usuarioId : ''}`;
    const method = modoEdicion ? 'PUT' : 'POST';

    try {
      const res = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + token,
        },
        body: JSON.stringify(data),
      });

      if (!res.ok) throw new Error('Error al guardar el usuario');

      alert('✅ Usuario guardado correctamente');
      form.reset();
      form.style.display = 'none';
      cargarUsuariosModal();
      modoEdicion = false;
      usuarioId = null;
    } catch (err) {
      alert('❌ ' + err.message);
    }
  });

  function obtenerRolesDesdeToken(token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.roles || [];
    } catch (err) {
      console.error('Error al decodificar token', err);
      return [];
    }
  }

  async function cargarUsuariosModal() {
    try {
      const res = await fetch('http://localhost:8080/api/usuarios', {
        headers: { Authorization: 'Bearer ' + token },
      });
      if (!res.ok) throw new Error('Error al cargar usuarios');

      const usuarios = await res.json();
      const tbody = document.querySelector('#tabla-usuarios-modal tbody');
      tbody.innerHTML = '';

      usuarios.forEach((u) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>${u.nombre}</td>
          <td>${u.email}</td>
          <td>${u.rol}</td>
          <td>${u.estado}</td>
          <td>${u.tallerNombre || '-'}</td>
          <td>
            <button class="editar-btn" data-id="${u.id}"><i class="fas fa-edit"></i></button>
            <button class="eliminar-btn" data-id="${u.id}"><i class="fas fa-trash"></i></button>
          </td>
        `;
        tbody.appendChild(tr);
      });

      document.querySelectorAll('.editar-btn').forEach(btn => {
        btn.addEventListener('click', () => cargarUsuario(btn.dataset.id));
      });

      document.querySelectorAll('.eliminar-btn').forEach(btn => {
        btn.addEventListener('click', () => eliminarUsuario(btn.dataset.id));
      });

    } catch (err) {
      alert('❌ ' + err.message);
    }
  }

  async function cargarUsuario(id) {
    try {
      const res = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
        headers: { Authorization: 'Bearer ' + token },
      });
      if (!res.ok) throw new Error('Error al cargar usuario');

      const u = await res.json();
      document.getElementById('nombre').value = u.nombre;
      document.getElementById('email').value = u.email;
      document.getElementById('rol').value = u.rol;
      document.getElementById('estado').value = u.estado;

      if (u.rol === 'MECANICO') {
        grupoMecanico.style.display = 'block';
        telefono.value = u.telefono || '';
        especialidad.value = u.especialidad || '';
        observaciones.value = u.observaciones || '';
      } else {
        grupoMecanico.style.display = 'none';
      }

      form.style.display = 'block';
      modal.style.display = 'none';
      overlay.classList.remove('active');

      modoEdicion = true;
      usuarioId = id;
    } catch (err) {
      alert('❌ ' + err.message);
    }
  }

  async function eliminarUsuario(id) {
    if (!confirm('¿Estás seguro de que querés eliminar este usuario?')) return;
    try {
      const res = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
        method: 'DELETE',
        headers: { Authorization: 'Bearer ' + token },
      });
      if (!res.ok) throw new Error('Error al eliminar');
      alert('✅ Usuario eliminado');
      cargarUsuariosModal();
    } catch (err) {
      alert('❌ ' + err.message);
    }
  }
});
