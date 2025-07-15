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

  const mensajeError = document.getElementById("mensaje-error-taller");
  const formulario = document.getElementById("form-cliente");

  if (!tallerId) {
    mensajeError && (mensajeError.style.display = "block");
  } else {
    formulario && (formulario.style.display = "block");
  }

  document.getElementById("cerrar-sesion")?.addEventListener("click", function (e) {
    e.preventDefault();
    localStorage.removeItem("token");
    localStorage.removeItem("tallerId");
    window.location.href = "login.html";
  });

  // Mostrar nombre del usuario logueado
  fetch("http://localhost:8080/api/usuarios/yo", {
    headers: {
      Authorization: "Bearer " + token
    }
  })
    .then(res => res.ok ? res.json() : Promise.reject())
    .then(user => {
      const span = document.getElementById("usuario-logueado");
      if (span && user?.nombre) span.textContent = "Hola, " + user.nombre;
    })
    .catch(() => {
      const span = document.getElementById("usuario-logueado");
      if (span) span.textContent = "Usuario desconocido";
    });

})();
