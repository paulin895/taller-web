
    window.addEventListener("DOMContentLoaded", () => {
  const fechaEntradaInput = document.getElementById("fechaEntrada");
  const hoy = new Date().toISOString().split("T")[0]; // formato YYYY-MM-DD
  fechaEntradaInput.value = hoy;
});

  let token = localStorage.getItem('token');
let tabla = null;
let mostrarCerradas = false;

const tallerId = localStorage.getItem("tallerId");

// Referencias a elementos
const clienteBusquedaInput = document.getElementById("clienteBusqueda");
const listaClientes = document.getElementById("listaClientes");
const vehiculoBusquedaInput = document.getElementById("vehiculoBusqueda");
const listaVehiculos = document.getElementById("listaVehiculos");
const mecanicoBusquedaInput = document.getElementById("mecanicoBusqueda");
const listaMecanicos = document.getElementById("listaMecanicos");

const mensaje = document.getElementById("mensaje");

let clientesCache = [];
let vehiculosCache = [];
let mecanicosCache = [];

function mostrarMensaje(msg, color = "green") {
 mensaje.textContent = msg;
 mensaje.style.color = color;
 setTimeout(() => { mensaje.textContent = ""; }, 4000);
}

async function cargarClientes() {
 try {
   const res = await fetch(`http://localhost:8080/api/clientes/taller/${tallerId}`, {
     headers: { Authorization: `Bearer ${token}` }
   });
   if (!res.ok) throw new Error("Error al obtener clientes");
   clientesCache = await res.json();
   listaClientes.innerHTML = '';
   clientesCache.forEach(c => {
     const option = document.createElement('option');
     option.value = c.nombreCompleto;
     listaClientes.appendChild(option);
   });
 } catch {
   mostrarMensaje("❌ Error al cargar clientes", "red");
 }
}

async function cargarMecanicos() {
 try {
   const res = await fetch(`http://localhost:8080/api/usuarios/taller/${tallerId}?rol=MECANICO`, {
     headers: { Authorization: `Bearer ${token}` }
   });
   if (!res.ok) throw new Error("Error al obtener mecánicos");
   mecanicosCache = await res.json();
   listaMecanicos.innerHTML = '';
   mecanicosCache.forEach(m => {
     const option = document.createElement('option');
     option.value = m.nombre;
     listaMecanicos.appendChild(option);
   });
 } catch {
   mostrarMensaje("❌ Error al cargar mecánicos", "red");
 }
}

async function cargarVehiculos(clienteId) {
 try {
   const res = await fetch(`http://localhost:8080/api/vehiculos/cliente/${clienteId}`, {
     headers: { Authorization: `Bearer ${token}` }
   });
   if (!res.ok) throw new Error("Error al obtener vehículos");
   vehiculosCache = await res.json();
   listaVehiculos.innerHTML = '';
   vehiculosCache.forEach(v => {
     const option = document.createElement('option');
     option.value = `${v.marca} ${v.modelo} - ${v.matricula}`;
     listaVehiculos.appendChild(option);
   });
 } catch {
   mostrarMensaje("❌ Error al cargar vehículos", "red");
 }
}

clienteBusquedaInput.addEventListener('input', async () => {
 const texto = clienteBusquedaInput.value.trim().toLowerCase();
 const clienteEncontrado = clientesCache.find(c => c.nombreCompleto.toLowerCase() === texto);
 if (clienteEncontrado) {
   await cargarVehiculos(clienteEncontrado.id);
   vehiculoBusquedaInput.disabled = false;
   mostrarMensaje("", "");
 } else {
   vehiculoBusquedaInput.value = '';
   vehiculoBusquedaInput.disabled = true;
   listaVehiculos.innerHTML = '';
   mostrarMensaje("⚠️ Cliente no encontrado en la lista.", "orange");
 }
});

vehiculoBusquedaInput.addEventListener('input', () => {
 const texto = vehiculoBusquedaInput.value.trim().toLowerCase();
 const vehiculoEncontrado = vehiculosCache.find(v => {
   const val = `${v.marca} ${v.modelo} - ${v.matricula}`.toLowerCase();
   return val === texto;
 });
 if (vehiculoEncontrado) {
   mostrarMensaje("", "");
 } else {
   mostrarMensaje("⚠️ Vehículo no encontrado en la lista.", "orange");
 }
});

async function listarOrdenes() {
 if (!token) {
   alert("No estás autenticado. Por favor, inicia sesión.");
   window.location.href = "login.html";
   return;
 }
 try {
   const res = await fetch("http://localhost:8080/api/ordenes", {
     headers: { Authorization: `Bearer ${token}` }
   });
   if (!res.ok) throw new Error(`HTTP error ${res.status}`);
   const ordenes = await res.json();
   const ordenesFiltradas = ordenes.filter(o =>
     mostrarCerradas ? true : (o.estado?.toLowerCase() !== 'cerrada')
   );

   if (tabla) {
     tabla.clear().destroy();
     tabla = null;
   }

   const tbody = document.querySelector("#tablaOrdenes tbody");
   tbody.innerHTML = "";

   ordenesFiltradas.forEach(o => {
     const estadoLower = (o.estado || '').toLowerCase();
     let colorEstado = '';
     switch (estadoLower) {
       case 'pendiente': colorEstado = '#f0ad4e'; break;
       case 'en_proceso': colorEstado = '#0275d8'; break;
       case 'finalizada_para_revisar': colorEstado = '#5bc0de'; break;
       case 'cerrada': colorEstado = '#5cb85c'; break;
       default: colorEstado = '#999';
     }
     const estadoHtml = `<span style="display:inline-block;padding:0.2em 0.5em; border-radius:0.3em; background:${colorEstado}; color:white; text-transform:capitalize; white-space: nowrap;">${o.estado ? o.estado.replace(/_/g, ' ') : ''}</span>`;

     const tr = document.createElement("tr");
     tr.innerHTML = `
       <td>${o.clienteNombreCompleto || 'Sin cliente'}</td>
       <td>${o.vehiculoDescripcion || ''}</td>
       <td>${o.mecanicoNombre || ''}</td>
       <td>${o.fechaEntrada || ''}</td>
       <td>${estadoHtml}</td>
       <td>${o.descripcion || ''}</td>
       <td>${o.kilometraje ?? ''}</td>
       <td>${o.precio != null ? `$${o.precio.toFixed(2)}` : ''}</td>
       <td>
         <button class="btn-accion btn-editar" data-id="${o.id}" title="Editar"><i class="fas fa-edit"></i></button>
         <button class="btn-accion btn-eliminar" data-id="${o.id}" title="Eliminar"><i class="fas fa-trash-alt"></i></button>
         <button class="btn-accion btn-imprimir" data-id="${o.id}" title="Imprimir"><i class="fas fa-print"></i></button>
       </td>
     `;
     tbody.appendChild(tr);
   });

   asignarEventosBotones();

   tabla = $('#tablaOrdenes').DataTable({
     responsive: true,
     pageLength: 5,
     lengthMenu: [5, 10, 20],
     dom: 'lrtip',

     language: {
       url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
     }
   });

   mostrarMensaje("Órdenes cargadas correctamente.");

 } catch (error) {
   console.error(error);
   mostrarMensaje("❌ Error al listar órdenes", "red");
 }
}

function asignarEventosBotones() {
 document.querySelectorAll(".btn-editar").forEach(btn => {
   btn.onclick = async () => {
     const id = btn.getAttribute("data-id");
     try {
       const res = await fetch(`http://localhost:8080/api/ordenes/${id}`, {
         headers: { Authorization: `Bearer ${token}` }
       });
       if (!res.ok) throw new Error(`Error HTTP ${res.status}`);
       const orden = await res.json();
       llenarFormulario(orden);
     } catch (error) {
       console.error(error);
       mostrarMensaje("Error al cargar orden para edición.", "red");
     }
   };
 });

 document.querySelectorAll(".btn-imprimir").forEach(btn => {
   btn.onclick = async () => {
     const id = btn.getAttribute("data-id");
     try {
       const res = await fetch(`http://localhost:8080/api/ordenes/${id}`, {
         headers: { Authorization: `Bearer ${token}` }
       });
       if (!res.ok) throw new Error(`Error HTTP ${res.status}`);
       const orden = await res.json();

       const win = window.open('', '', 'width=800,height=600');
       win.document.write(`
         <html>
         <head>
           <title>Orden #${orden.id}</title>
           <style>
             body {
               font-family: 'Arial', sans-serif;
               padding: 30px;
               color: #333;
             }
             .header {
               display: flex;
               align-items: center;
               justify-content: space-between;
               border-bottom: 2px solid #005aef;
               margin-bottom: 20px;
               padding-bottom: 10px;
             }
             .header img {
               height: 50px;
             }
             .header h2 {
               color: #005aef;
               margin: 0;
             }
             .orden-info {
               margin-top: 20px;
             }
             .orden-info p {
               margin: 5px 0;
               font-size: 16px;
             }
             .orden-info strong {
               display: inline-block;
               width: 160px;
             }
             .footer {
               margin-top: 40px;
               text-align: center;
               font-size: 14px;
               color: #777;
             }
             .firma {
               margin-top: 60px;
               text-align: left;
             }
             .firma hr {
               width: 200px;
               margin-left: 0;
             }
           </style>
         </head>
         <body>
           <div class="header">
             <img src="https://i.ibb.co/pRsxY2f/logo-autogest.png" alt="Logo AutoGest" />
             <h2>AutoGest – Orden de Servicio</h2>
           </div>

           <div class="orden-info">
             <p><strong>Número de Orden:</strong> #${orden.id}</p>
             <p><strong>Cliente:</strong> ${orden.clienteNombreCompleto}</p>
             <p><strong>Vehículo:</strong> ${orden.vehiculoDescripcion}</p>
             <p><strong>Mecánico:</strong> ${orden.mecanicoNombre}</p>
             <p><strong>Fecha de Entrada:</strong> ${orden.fechaEntrada}</p>
             <p><strong>Estado:</strong> ${orden.estado.replace(/_/g, ' ')}</p>
             <p><strong>Descripción:</strong> ${orden.descripcion}</p>
             <p><strong>Kilometraje:</strong> ${orden.kilometraje ?? ''}</p>
             <p><strong>Precio:</strong> ${orden.precio != null ? `$${orden.precio.toFixed(2)}` : ''}</p>
             <p><strong>Observaciones:</strong> ${orden.observaciones || '-'}</p>
           </div>

           <div class="firma">
             <p>Firma del responsable:</p>
             <hr/>
           </div>

           <div class="footer">
             &copy; ${new Date().getFullYear()} AutoGest – DePunta Software
           </div>
         </body>
         </html>
       `);
       win.document.close();
       win.print();
     } catch (error) {
       console.error(error);
       mostrarMensaje("❌ Error al imprimir orden", "red");
     }
   };
 });



 document.querySelectorAll(".btn-eliminar").forEach(btn => {
   btn.onclick = async () => {
     const id = btn.getAttribute("data-id");
     if (confirm("¿Seguro que quieres eliminar esta orden?")) {
       try {
         const res = await fetch(`http://localhost:8080/api/ordenes/${id}`, {
           method: "DELETE",
           headers: { Authorization: `Bearer ${token}` }
         });
         if (!res.ok) throw new Error(`Error HTTP ${res.status}`);
         mostrarMensaje("Orden eliminada correctamente.");
         listarOrdenes();
       } catch (error) {
         console.error(error);
         mostrarMensaje("Error al eliminar orden.", "red");
       }
     }
   };
 });
}

function llenarFormulario(orden) {
 document.getElementById("ordenId").value = orden.id || "";
 document.getElementById("clienteBusqueda").value = orden.clienteNombreCompleto || "";
 // Al poner cliente se carga automáticamente vehículos y se habilita vehículo
 const clienteEncontrado = clientesCache.find(c => c.nombreCompleto.toLowerCase() === (orden.clienteNombreCompleto || "").toLowerCase());
 if(clienteEncontrado) {
   cargarVehiculos(clienteEncontrado.id).then(() => {
     document.getElementById("vehiculoBusqueda").value = orden.vehiculoDescripcion || "";
     document.getElementById("vehiculoBusqueda").disabled = false;
   });
 } else {
   document.getElementById("vehiculoBusqueda").value = orden.vehiculoDescripcion || "";
   document.getElementById("vehiculoBusqueda").disabled = true;
 }
 document.getElementById("mecanicoBusqueda").value = orden.mecanicoNombre || "";
 document.getElementById("fechaEntrada").value = orden.fechaEntrada || "";
 document.getElementById("estado").value = orden.estado || "PENDIENTE";
 document.getElementById("descripcion").value = orden.descripcion || "";
 document.getElementById("kilometraje").value = orden.kilometraje ?? "";
 document.getElementById("precio").value = orden.precio ?? "";
 document.getElementById("observaciones").value = orden.observaciones || "";
 document.querySelector(".form-container").classList.add("visible");
 window.scrollTo({ top: document.querySelector(".form-container").offsetTop - 20, behavior: 'smooth' });

 // Campos de cierre
 if (document.getElementById("fechaSalida")) {
  document.getElementById("fechaSalida").value = orden.fechaSalida || new Date().toISOString().split("T")[0];
}
if (document.getElementById("descripcionCierre")) {
  document.getElementById("descripcionCierre").value = orden.descripcionCierre || "";
}


 toggleCamposCierre();
 toggleCampoPrecio();

 document.getElementById("form-title").textContent = `Editar Orden #${orden.id}`;
}

function limpiarFormulario() {
 document.getElementById("formOrden").reset();
 document.getElementById("vehiculoBusqueda").disabled = true;
 document.getElementById("ordenId").value = "";
 document.getElementById("form-title").textContent = "Nueva Orden de Servicio";
 toggleCamposCierre();
 toggleCampoPrecio();
 mostrarMensaje("");
}

function toggleCamposCierre() {
 const estado = document.getElementById("estado").value;
 const mostrar = (estado === "CERRADA");

 const fechaSalida = document.getElementById("fechaSalida");
 const labelFechaSalida = document.getElementById("labelFechaSalida");
 const descripcionCierre = document.getElementById("descripcionCierre");
 const labelDescripcionCierre = document.getElementById("labelDescripcionCierre");
 const precio = document.getElementById("precio");
 const labelPrecio = document.getElementById("labelPrecio");

 if(fechaSalida && labelFechaSalida) {
   fechaSalida.style.display = mostrar ? "block" : "none";
   labelFechaSalida.style.display = mostrar ? "block" : "none";
   fechaSalida.required = mostrar;
 }
 if(descripcionCierre && labelDescripcionCierre) {
   descripcionCierre.style.display = mostrar ? "block" : "none";
   labelDescripcionCierre.style.display = mostrar ? "block" : "none";
   descripcionCierre.required = mostrar;
 }
 if(precio && labelPrecio) {
   precio.style.display = mostrar ? "block" : "none";
   labelPrecio.style.display = mostrar ? "block" : "none";
   precio.required = mostrar;
 }
}

function toggleCampoPrecio() {
 const estado = document.getElementById("estado").value;
 const precio = document.getElementById("precio");
 precio.required = (estado === "CERRADA");
}

// Cambiar el manejo del checkbox (en lugar del botón toggle)
const chkMostrarCerradas = document.getElementById("chkMostrarCerradas");
chkMostrarCerradas.addEventListener("change", () => {
 mostrarCerradas = chkMostrarCerradas.checked;
 listarOrdenes();
});

// Evento para cambiar campos al cambiar estado
document.getElementById("estado").addEventListener("change", () => {
 toggleCamposCierre();
 toggleCampoPrecio();
});

// Envío del formulario
document.getElementById("formOrden").addEventListener("submit", async (e) => {
 e.preventDefault();

 const clienteNombre = clienteBusquedaInput.value.trim();
 const vehiculoStr = vehiculoBusquedaInput.value.trim();
 const mecanicoNombre = mecanicoBusquedaInput.value.trim();
 const fechaEntrada = document.getElementById("fechaEntrada").value;
 const descripcion = document.getElementById("descripcion").value.trim();
 const estado = document.getElementById("estado").value;
 const kilometraje = document.getElementById("kilometraje").value ? parseInt(document.getElementById("kilometraje").value) : null;
 const precio = document.getElementById("precio").value ? parseFloat(document.getElementById("precio").value) : null;
 const observaciones = document.getElementById("observaciones").value.trim();
 const ordenId = document.getElementById("ordenId").value;

 if (!clienteNombre) return mostrarMensaje("⚠️ Debes seleccionar un cliente válido", "red");
 if (!vehiculoStr) return mostrarMensaje("⚠️ Debes seleccionar un vehículo válido", "red");
 if (!mecanicoNombre) return mostrarMensaje("⚠️ Debes seleccionar un mecánico válido", "red");
 if (!fechaEntrada) return mostrarMensaje("⚠️ Debes ingresar la fecha de entrada", "red");
 if (!descripcion) return mostrarMensaje("⚠️ Debes ingresar la descripción", "red");
 if (estado === "CERRADA" && !precio) return mostrarMensaje("⚠️ Debes ingresar el costo para órdenes cerradas", "red");

 const cliente = clientesCache.find(c => c.nombreCompleto.toLowerCase() === clienteNombre.toLowerCase());
 if (!cliente) return mostrarMensaje("⚠️ Cliente no válido", "red");

 const vehiculo = vehiculosCache.find(v => {
   const val = `${v.marca} ${v.modelo} - ${v.matricula}`.toLowerCase();
   return val === vehiculoStr.toLowerCase();
 });
 if (!vehiculo) return mostrarMensaje("⚠️ Vehículo no válido", "red");

 const mecanico = mecanicosCache.find(m => m.nombre.toLowerCase() === mecanicoNombre.toLowerCase());
 if (!mecanico) return mostrarMensaje("⚠️ Mecánico no válido", "red");

const fechaSalida = document.getElementById("fechaSalida").value || null;
const descripcionCierre = document.getElementById("descripcionCierre").value.trim() || null;

// Opcionales (por ahora vacíos o null si no los usás todavía)
const condicionFinal = null;
const notaInterna = "";

const data = {
...(ordenId && { id: parseInt(ordenId) }),
vehiculoId: vehiculo.id,
mecanicoId: mecanico.id,
fechaEntrada,
descripcion,
estado,
kilometraje,
precio,
observaciones,
fechaSalida,
descripcionCierre,
condicionFinal,
notaInterna
};

 let url = "http://localhost:8080/api/ordenes";
 let method = "POST";

 if (ordenId) {
   url += `/${ordenId}`;
   method = "PUT";
 }

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
     mostrarMensaje(ordenId ? "✅ Orden actualizada" : "✅ Orden creada", "green");
     limpiarFormulario();
     mostrarCerradas = false;
     chkMostrarCerradas.checked = false;
     listarOrdenes();
   } else {
     mostrarMensaje("❌ Error al guardar orden", "red");
   }
 } catch {
   mostrarMensaje("❌ Error de conexión", "red");
 }
});

document.getElementById("btnLimpiar").addEventListener("click", () => {
 limpiarFormulario();
  document.querySelector(".form-container").classList.remove("visible");
});

document.getElementById("cerrar-sesion").addEventListener("click", e => {
 e.preventDefault();
 localStorage.removeItem("token");
 localStorage.removeItem("tallerId");
 window.location.href = "login.html";
});

async function inicializar() {
 await cargarClientes();
 await cargarMecanicos();
 vehiculoBusquedaInput.disabled = true;
 listarOrdenes();
 toggleCamposCierre();
 toggleCampoPrecio();
}

document.getElementById("btn-agregar-orden").addEventListener("click", () => {
  const formContainer = document.querySelector(".form-container");
  formContainer.classList.add("visible");
  limpiarFormulario(); // limpia el form por si quedó algo cargado
  window.scrollTo({ top: formContainer.offsetTop - 20, behavior: 'smooth' });
});

inicializar();

