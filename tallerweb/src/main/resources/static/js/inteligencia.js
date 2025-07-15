 document.addEventListener("DOMContentLoaded", () => {
    const boton = document.querySelector(".form-ai .btn");
    const preguntaInput = document.getElementById('pregunta');
    const respuestaDiv = document.getElementById('respuesta');

    boton.addEventListener("click", () => {
      const pregunta = preguntaInput.value.trim();

      if (!pregunta) {
        respuestaDiv.style.display = 'block';
        respuestaDiv.classList.add('error');
        respuestaDiv.innerHTML = '⚠️ Por favor, escribí una pregunta.';
        return;
      }

      respuestaDiv.style.display = 'block';
      respuestaDiv.classList.remove('error');
      respuestaDiv.innerHTML = '⏳ Procesando tu pregunta...';

      fetch('http://localhost:8080/api/ia', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pregunta })
      })
        .then(response => {
          if (!response.ok) throw new Error("Error HTTP " + response.status);
          return response.json();
        })
        .then(data => {
          if (data && data.respuesta) {
            respuestaDiv.innerHTML = '💬 ' + data.respuesta;
          } else {
            respuestaDiv.classList.add('error');
            respuestaDiv.innerHTML = '⚠️ La respuesta del servidor fue vacía o malformada.';
          }
        })
        .catch(error => {
          console.error('Error al consultar IA:', error);
          respuestaDiv.classList.add('error');
          respuestaDiv.innerHTML = '⚠️ Ocurrió un error al consultar: ' + error.message;
        });
    });
  });