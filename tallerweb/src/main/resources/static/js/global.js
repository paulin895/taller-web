document.addEventListener("DOMContentLoaded", () => {
  const menuToggle = document.getElementById("menu-toggle");
  const sidebar = document.getElementById("sidebar");
  const overlay = document.getElementById("overlay");

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

  const token = localStorage.getItem("token");
  if (token) {
    fetch("http://localhost:8080/api/usuarios/yo", {
      headers: {
        Authorization: "Bearer " + token
      }
    })
      .then(res => res.ok ? res.json() : Promise.reject())
      .then(user => {
        document.getElementById("usuario-logueado")?.textContent = "Usuario: " + user.nombre;
        document.getElementById("usuario-logueado-movil")?.textContent = "Usuario: " + user.nombre;
      })
      .catch(() => {
        document.getElementById("usuario-logueado")?.textContent = "Usuario desconocido";
        document.getElementById("usuario-logueado-movil")?.textContent = "Usuario desconocido";
      });
  }

  const cerrarSesion = document.getElementById("cerrar-sesion");
  cerrarSesion?.addEventListener("click", (e) => {
    e.preventDefault();
    localStorage.removeItem("token");
    localStorage.removeItem("tallerId");
    window.location.href = "login.html";
  });
});
