package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.Cliente;
import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.ClienteRepository;
import com.taller.tallerweb.repository.TallerRepository;
import com.taller.tallerweb.repository.UsuarioRepository;
import com.taller.tallerweb.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // Permite llamadas desde cualquier frontend
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TallerRepository tallerRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Método para obtener usuario logueado desde token
    private Usuario obtenerUsuarioLogueado(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String email = jwtUtil.extractUsername(token.substring(7));
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Listar clientes - mecánicos solo ven clientes de su taller
    @GetMapping
    public ResponseEntity<?> getAllClientes(@RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        List<Cliente> clientes;

        if (usuarioLogueado.getRol() == Usuario.Rol.MECANICO) {
            if (usuarioLogueado.getTaller() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene taller asignado");
            }
            clientes = clienteRepository.findByTallerId(usuarioLogueado.getTaller().getId());
        } else {
            // ADMIN y SUPERADMIN pueden ver todos
            clientes = clienteRepository.findAll();
        }

        return ResponseEntity.ok(clientes);
    }

    // Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        Optional<Cliente> optCliente = clienteRepository.findById(id);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }
        Cliente cliente = optCliente.get();

        // Validar que mecánico solo pueda ver clientes de su taller
        if (usuarioLogueado.getRol() == Usuario.Rol.MECANICO) {
            if (cliente.getTaller() == null || !cliente.getTaller().getId().equals(usuarioLogueado.getTaller().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para ver este cliente");
            }
        }

        return ResponseEntity.ok(cliente);
    }

    // Crear cliente - solo ADMIN y SUPERADMIN
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente, @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        if (usuarioLogueado.getRol() != Usuario.Rol.ADMIN && usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos para crear clientes");
        }

        if (cliente.getTaller() != null) {
            Long idTaller = cliente.getTaller().getId();
            Taller tallerExistente = tallerRepository.findById(idTaller)
                    .orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + idTaller));
            cliente.setTaller(tallerExistente);
        }

        Cliente nuevoCliente = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // Actualizar cliente - solo ADMIN y SUPERADMIN
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails,
                                           @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        if (usuarioLogueado.getRol() != Usuario.Rol.ADMIN && usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos para actualizar clientes");
        }

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }

        cliente.setNombreCompleto(clienteDetails.getNombreCompleto());
        cliente.setCi(clienteDetails.getCi());
        cliente.setTelefono(clienteDetails.getTelefono());
        cliente.setCorreo(clienteDetails.getCorreo());
        cliente.setDireccion(clienteDetails.getDireccion());
        cliente.setObservaciones(clienteDetails.getObservaciones());

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return ResponseEntity.ok(clienteActualizado);
    }

    // Eliminar cliente - solo ADMIN y SUPERADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        if (usuarioLogueado.getRol() != Usuario.Rol.ADMIN && usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos para eliminar clientes");
        }

        Optional<Cliente> optCliente = clienteRepository.findById(id);
        if (optCliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }

        clienteRepository.deleteById(id);
        return ResponseEntity.ok("Cliente eliminado");
    }

    // Listar clientes por taller - igual, filtro para mecánico
    @GetMapping("/taller/{tallerId}")
    public ResponseEntity<?> getClientesPorTaller(@PathVariable Long tallerId, @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        // Validar que mecánico solo vea clientes de su taller
        if (usuarioLogueado.getRol() == Usuario.Rol.MECANICO &&
                (usuarioLogueado.getTaller() == null || !usuarioLogueado.getTaller().getId().equals(tallerId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para ver estos clientes");
        }

        List<Cliente> clientes = clienteRepository.findByTallerId(tallerId);
        return ResponseEntity.ok(clientes);
    }
}
