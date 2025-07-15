document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  const tallerId = localStorage.getItem("tallerId");

  if (!token || !tallerId) {
    alert("No estás autenticado. Redirigiendo al login...");
    window.location.href = "login.html";
    return;
  }

  const tabla = $("#tabla-reportes-vehiculos").DataTable({
    dom: 'Bfrtip',
    buttons: ['copy', 'excel', 'pdf', 'print'],
    responsive: true,
    language: {
      decimal: ",",
      thousands: ".",
      info: "Mostrando _START_ a _END_ de _TOTAL_ vehículos",
      infoEmpty: "Mostrando 0 a 0 de 0 vehículos",
      infoFiltered: "(filtrado de _MAX_ vehículos totales)",
      lengthMenu: "Mostrar _MENU_ vehículos",
      loadingRecords: "Cargando...",
      processing: "Procesando...",
      search: "Buscar:",
      zeroRecords: "No se encontraron resultados",
      emptyTable: "No hay vehículos registrados",
      paginate: {
        first: "Primero",
        previous: "Anterior",
        next: "Siguiente",
        last: "Último"
      },
      buttons: {
        copy: "Copiar",
        excel: "Exportar a Excel",
        pdf: "Exportar a PDF",
        print: "Imprimir"
      }
    },
    columns: [
      { data: "nombreCliente" },
      { data: "marca" },
      { data: "modelo" },
      { data: "matricula" },
      { data: "anio" },
      { data: "color" },
      { data: "observaciones" }
    ],
    data: []
  });

  async function cargarVehiculos() {
    try {
      const res = await fetch(`http://localhost:8080/api/vehiculos/taller/${tallerId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (!res.ok) throw new Error("Error al obtener vehículos");

      const vehiculos = await res.json();

      // Asegurar que cada vehículo tenga todas las propiedades necesarias
      vehiculos.forEach(v => {
        v.nombreCliente ??= "Sin cliente";
        v.marca ??= "";
        v.modelo ??= "";
        v.matricula ??= "";
        v.anio ??= "";
        v.color ??= "";
        v.observaciones ??= ""; // 👈 esta línea evita el error de la columna 6
      });

      tabla.clear().rows.add(vehiculos).draw();

      // Estadística
      document.getElementById("total-vehiculos").textContent = vehiculos.length;

    } catch (error) {
      console.error("Error cargando vehículos:", error);
      alert("❌ Error al cargar los vehículos.");
    }
  }


  cargarVehiculos();
});
