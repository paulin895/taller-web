package com.taller.tallerweb.controller;

import com.taller.tallerweb.dto.OrdenServicioDTO;
import com.taller.tallerweb.mapper.OrdenServicioMapper;
import com.taller.tallerweb.model.OrdenServicio;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.OrdenServicioRepository;
import com.taller.tallerweb.repository.UsuarioRepository;
import com.taller.tallerweb.repository.VehiculoRepository;
import com.taller.tallerweb.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenServicioController {

    @Autowired
    private OrdenServicioRepository ordenServicioRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nueva orden (solo admin)
    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenServicioDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String rol = userPrincipal.getAuthorities().iterator().next().getAuthority();

        if (!"ROLE_ADMIN".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo admin puede crear órdenes");
        }

        var vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehículo no encontrado"));

        var mecanico = usuarioRepository.findById(dto.getMecanicoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mecánico no encontrado"));

        if (mecanico.getRol() != Usuario.Rol.MECANICO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es un mecánico");
        }

        OrdenServicio orden = new OrdenServicio();
        orden.setVehiculo(vehiculo);
        orden.setMecanico(mecanico);
        orden.setTaller(vehiculo.getTaller());
        orden.setDescripcion(dto.getDescripcion());
        orden.setFechaEntrada(dto.getFechaEntrada());
        orden.setObservaciones(dto.getObservaciones());
        orden.setEstado(dto.getEstado());
        orden.setKilometraje(dto.getKilometraje());
        orden.setFechaSalida(dto.getFechaSalida());
        orden.setDescripcionCierre(dto.getDescripcionCierre());
        orden.setCondicionFinal(dto.getCondicionFinal());
        orden.setPrecio(dto.getPrecio());
        orden.setNotaInterna(dto.getNotaInterna());

        ordenServicioRepository.save(orden);
        return ResponseEntity.ok().build();
    }

    // Listar órdenes según rol
    @GetMapping
    public List<OrdenServicioDTO> listarOrdenes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Long tallerId = userPrincipal.getTaller().getId();
        Long usuarioId = userPrincipal.getId();
        String rol = userPrincipal.getAuthorities().iterator().next().getAuthority();

        List<OrdenServicio> ordenes;

        if ("ROLE_ADMIN".equals(rol)) {
            ordenes = ordenServicioRepository.findByTallerId(tallerId);
        } else if ("ROLE_MECANICO".equals(rol)) {
            ordenes = ordenServicioRepository.findByTallerIdAndMecanicoId(tallerId, usuarioId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        return OrdenServicioMapper.toDTOList(ordenes);
    }



    // Eliminar orden (solo admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOrden(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String rol = userPrincipal.getAuthorities().iterator().next().getAuthority();

        if (!"ROLE_ADMIN".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo admin puede eliminar órdenes");
        }

        OrdenServicio orden = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        ordenServicioRepository.delete(orden);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public OrdenServicioDTO obtenerOrden(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String rol = userPrincipal.getAuthorities().iterator().next().getAuthority();

        OrdenServicio orden = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        // Validar acceso
        if ("ROLE_ADMIN".equals(rol)) {
            // Puede ver todas
        } else if ("ROLE_MECANICO".equals(rol)) {
            if (!orden.getMecanico().getId().equals(userPrincipal.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede acceder a esta orden");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        return OrdenServicioMapper.toDTO(orden);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarOrden(@PathVariable Long id, @RequestBody OrdenServicioDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String rol = userPrincipal.getAuthorities().iterator().next().getAuthority();

        OrdenServicio orden = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        if ("ROLE_ADMIN".equals(rol)) {
            // Admin puede modificar todo
            if (dto.getVehiculoId() != null) {
                var vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehículo no encontrado"));
                orden.setVehiculo(vehiculo);
                orden.setTaller(vehiculo.getTaller());
            }

            if (dto.getMecanicoId() != null) {
                var mecanico = usuarioRepository.findById(dto.getMecanicoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mecánico no encontrado"));
                if (mecanico.getRol() != Usuario.Rol.MECANICO) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es un mecánico");
                }
                orden.setMecanico(mecanico);
            }

            orden.setDescripcion(dto.getDescripcion());
            orden.setFechaEntrada(dto.getFechaEntrada());
            orden.setObservaciones(dto.getObservaciones());
            orden.setEstado(dto.getEstado());
            orden.setKilometraje(dto.getKilometraje());
            orden.setFechaSalida(dto.getFechaSalida());
            orden.setDescripcionCierre(dto.getDescripcionCierre());
            orden.setCondicionFinal(dto.getCondicionFinal());
            orden.setPrecio(dto.getPrecio());
            orden.setNotaInterna(dto.getNotaInterna());

            // Si querés podés poner acá un chequeo para que solo admin pueda poner estado CERRADA,
            // pero si llega acá ya sabemos que es admin.

        } else if ("ROLE_MECANICO".equals(rol)) {
            // El mecánico solo puede modificar su orden
            if (!orden.getMecanico().getId().equals(userPrincipal.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede modificar esta orden");
            }
            // Solo puede marcarla como FINALIZADA_PARA_REVISAR
            if (dto.getEstado() != OrdenServicio.EstadoOrden.FINALIZADA) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puede marcar la orden como FINALIZADA_PARA_REVISAR");
            }
            orden.setEstado(OrdenServicio.EstadoOrden.FINALIZADA);

            // No permitas que mecánico modifique otras propiedades por seguridad

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        ordenServicioRepository.save(orden);
        return ResponseEntity.ok().build();
    }


}
