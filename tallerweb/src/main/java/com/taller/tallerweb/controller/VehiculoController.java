package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.Vehiculo;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.dto.VehiculoDTO;
import com.taller.tallerweb.repository.VehiculoRepository;
import com.taller.tallerweb.repository.UsuarioRepository;
import com.taller.tallerweb.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Listar vehículos por taller con DTO
    @GetMapping("/taller/{tallerId}")
    public ResponseEntity<?> getVehiculosPorTaller(
            @PathVariable Long tallerId,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuarioLogueado.getTaller() == null || !usuarioLogueado.getTaller().getId().equals(tallerId))) {
            return ResponseEntity.status(403).body("No tiene permiso para ver estos vehículos");
        }

        List<Vehiculo> vehiculos = vehiculoRepository.findByTallerId(tallerId);
        List<VehiculoDTO> vehiculoDTOs = vehiculos.stream()
                .map(this::convertirADTO)
                .toList();

        return ResponseEntity.ok(vehiculoDTOs);
    }

    // Listar vehículos por cliente con DTO
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getVehiculosPorCliente(
            @PathVariable Long clienteId,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        List<Vehiculo> vehiculos = vehiculoRepository.findByClienteId(clienteId);
        List<VehiculoDTO> vehiculoDTOs = vehiculos.stream()
                .map(this::convertirADTO)
                .toList();

        return ResponseEntity.ok(vehiculoDTOs);
    }

    // Obtener vehículo por ID con validación, sin DTO (detalle completo)
    @GetMapping("/{id}")
    public ResponseEntity<?> getVehiculoById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
        if (vehiculoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehiculo vehiculo = vehiculoOpt.get();

        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuarioLogueado.getTaller() == null || !usuarioLogueado.getTaller().getId().equals(vehiculo.getTaller().getId()))) {
            return ResponseEntity.status(403).body("No tiene permiso para ver este vehículo");
        }

        return ResponseEntity.ok(vehiculo); // Detalle completo
    }

    // Crear vehículo
    @PostMapping
    public ResponseEntity<?> createVehiculo(
            @RequestBody Vehiculo vehiculo,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        Vehiculo creado = vehiculoRepository.save(vehiculo);
        return ResponseEntity.ok(convertirADTO(creado));
    }

    // Actualizar vehículo
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehiculo(
            @PathVariable Long id,
            @RequestBody Vehiculo vehiculoDetails,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
        if (vehiculoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehiculo vehiculo = vehiculoOpt.get();

        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuarioLogueado.getTaller() == null || !usuarioLogueado.getTaller().getId().equals(vehiculo.getTaller().getId()))) {
            return ResponseEntity.status(403).body("No tiene permiso para modificar este vehículo");
        }

        vehiculo.setMarca(vehiculoDetails.getMarca());
        vehiculo.setModelo(vehiculoDetails.getModelo());
        vehiculo.setAnio(vehiculoDetails.getAnio());
        vehiculo.setMatricula(vehiculoDetails.getMatricula());
        vehiculo.setColor(vehiculoDetails.getColor());
        vehiculo.setObservaciones(vehiculoDetails.getObservaciones());
        vehiculo.setCliente(vehiculoDetails.getCliente());
        vehiculo.setTaller(vehiculoDetails.getTaller());

        Vehiculo actualizado = vehiculoRepository.save(vehiculo);
        return ResponseEntity.ok(convertirADTO(actualizado));
    }

    // Eliminar vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehiculo(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
        if (vehiculoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehiculo vehiculo = vehiculoOpt.get();

        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuarioLogueado.getTaller() == null || !usuarioLogueado.getTaller().getId().equals(vehiculo.getTaller().getId()))) {
            return ResponseEntity.status(403).body("No tiene permiso para eliminar este vehículo");
        }

        vehiculoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Extraer usuario desde token JWT
    private Usuario obtenerUsuarioLogueado(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Convertir entidad Vehiculo a DTO incluyendo nombreCliente
    private VehiculoDTO convertirADTO(Vehiculo v) {
        String nombreCliente = (v.getCliente() != null && v.getCliente().getNombreCompleto() != null)
                ? v.getCliente().getNombreCompleto()
                : "Sin cliente";

        return new VehiculoDTO(
                v.getId(),
                v.getMarca(),
                v.getModelo(),
                v.getAnio(),
                v.getMatricula(),
                v.getColor(),
                nombreCliente
        );
    }
}
