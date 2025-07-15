document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  const tallerId = localStorage.getItem("tallerId");

  if (!token || !tallerId) {
    alert("No estás autenticado. Redirigiendo al login...");
    window.location.href = "login.html";
    return;
  }

  const tabla = $("#tabla-reportes-clientes").DataTable({
    dom: 'Bfrtip',
    buttons: [
      'copy', 'excel', 'pdf', 'print'
    ],
    responsive: true,
    language: {
       url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
    },
    columns: [
      { data: "nombreCompleto" },
      { data: "ci" },
      { data: "telefono" },
      { data: "correo" },
      { data: "direccion" },
      { data: "observaciones" }
    ],
    data: [] // se llenará dinámicamente
  });

  function cargarClientes() {
    fetch(`http://localhost:8080/api/clientes/taller/${tallerId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(response => {
        if (!response.ok) throw new Error("Error al obtener clientes");
        return response.json();
      })
      .then(clientes => {
        tabla.clear().rows.add(clientes).draw();

        // Estadísticas
        document.getElementById("total-clientes").textContent = clientes.length;

        const hoy = new Date();
        const mesActual = hoy.getMonth();
        const anioActual = hoy.getFullYear();

        const clientesEsteMes = clientes.filter(c => {
          if (!c.fechaRegistro) return false;
          const fecha = new Date(c.fechaRegistro);
          return fecha.getMonth() === mesActual && fecha.getFullYear() === anioActual;
        });

        document.getElementById("clientes-mes").textContent = clientesEsteMes.length;
      })
      .catch(error => {
        console.error("Error cargando clientes:", error);
        alert("Hubo un problema al cargar los datos.");
      });
  }

  cargarClientes();
});
