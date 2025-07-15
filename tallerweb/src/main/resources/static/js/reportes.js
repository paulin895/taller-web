// static/js/reportes.js
// ------------------------------------------------------------
//  Script exclusivo para reportes.html
// ------------------------------------------------------------

// -------------------- Variables globales --------------------
const token    = localStorage.getItem("token");
const tallerId = localStorage.getItem("tallerId");

// -------------------- Funciones auxiliares ------------------
/**
 * Decodifica el JWT y devuelve un array con los roles
 * @param {string} jwt
 * @returns {string[]} roles
 */
function obtenerRolesDesdeToken(jwt) {
  try {
    const payloadBase64 = jwt.split(".")[1];
    // Normalizamos Base64-URL a Base64
    const base64 = payloadBase64.replace(/-/g, "+").replace(/_/g, "/");
    const json   = decodeURIComponent(
      atob(base64)
        .split("")
        .map(c => `%${("00" + c.charCodeAt(0).toString(16)).slice(-2)}`)
        .join("")
    );
    const payload = JSON.parse(json);
    return payload.roles || [];
  } catch (e) {
    console.error("Error decodificando token:", e);
    return [];
  }
}

/**
 * Limpia credenciales y redirige al login
 */
function forzarLogout() {
  localStorage.removeItem("token");
  localStorage.removeItem("tallerId");
  window.location.href = "login.html";
}

// -------------------- IIFE principal ------------------------
(function () {
  // 1. Verificamos presencia de token
  if (!token) {
    alert("No estás autenticado. Serás redirigido al login.");
    forzarLogout();
    return;
  }

  // 2. Control de roles (dejamos pasar ADMIN o SUPERADMIN)
  const rolesOK = ["ROLE_ADMIN", "ROLE_SUPERADMIN"];
  const roles   = obtenerRolesDesdeToken(token);

  if (!roles.some(r => rolesOK.includes(r))) {
    alert("No tenés permisos para acceder a los reportes.");
    window.location.href = "panel-admin.html";
    return;
  }

  // 3. Evento Cerrar Sesión
  document.getElementById("cerrar-sesion")?.addEventListener("click", e => {
    e.preventDefault();
    forzarLogout();
  });

  // 4. Mostrar nombre del usuario logueado
  fetch("http://localhost:8080/api/usuarios/yo", {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => (res.ok ? res.json() : Promise.reject()))
    .then(user => {
      const span = document.getElementById("usuario-logueado");
      if (span && user?.nombre) span.textContent = `Hola, ${user.nombre}`;
    })
    .catch(() => {
      const span = document.getElementById("usuario-logueado");
      if (span) span.textContent = "Usuario desconocido";
    });

  // 5. Menú hamburguesa / overlay (por si no lo hacés en global.js)
  const menuToggle = document.getElementById("menu-toggle");
  const sidebar    = document.getElementById("sidebar");
  const overlay    = document.getElementById("overlay");

  if (menuToggle && sidebar && overlay) {
    menuToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
      overlay.classList.toggle("active");

      const icon = menuToggle.querySelector("i");
      if (icon) {
        icon.classList.toggle("fa-bars");
        icon.classList.toggle("fa-times");
      }
    });

    overlay.addEventListener("click", () => {
      sidebar.classList.remove("open");
      overlay.classList.remove("active");

      const icon = menuToggle.querySelector("i");
      if (icon) {
        icon.classList.remove("fa-times");
        icon.classList.add("fa-bars");
      }
    });
  }
})();
