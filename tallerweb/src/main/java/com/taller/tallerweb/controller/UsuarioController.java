package com.taller.tallerweb.controller;

import com.taller.tallerweb.dto.UsuarioDTO;
import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.TallerRepository;
import com.taller.tallerweb.repository.UsuarioRepository;
import com.taller.tallerweb.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.taller.tallerweb.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TallerRepository tallerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // DTO -> Usuario
    private Usuario fromDTO(UsuarioDTO dto, Usuario usuarioExistente) {
        usuarioExistente.setNombre(dto.getNombre());
        usuarioExistente.setEmail(dto.getEmail());
        usuarioExistente.setRol(dto.getRol());
        usuarioExistente.setEstado(dto.getEstado());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuarioExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getTallerId() != null) {
            Optional<Taller> optTaller = tallerRepository.findById(dto.getTallerId());
            if (optTaller.isPresent()) {
                usuarioExistente.setTaller(optTaller.get());
            } else {
                usuarioExistente.setTaller(null);
            }
        } else {
            usuarioExistente.setTaller(null);
        }

        // Nuevos campos para mecánicos
        usuarioExistente.setTelefono(dto.getTelefono());
        usuarioExistente.setEspecialidad(dto.getEspecialidad());
        usuarioExistente.setObservaciones(dto.getObservaciones());
        usuarioExistente.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        return usuarioExistente;
    }
    @GetMapping("/taller/{tallerId}")
    public ResponseEntity<?> getUsuariosPorTallerYRol(
            @PathVariable Long tallerId,
            @RequestParam(required = false) String rol,
            @RequestHeader("Authorization") String token) {

        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        // Debug: imprimir datos del usuario y taller solicitado
        System.out.println("Usuario logueado email: " + usuarioLogueado.getEmail());
        System.out.println("Usuario logueado rol: " + usuarioLogueado.getRol());
        System.out.println("Usuario logueado taller id: " + (usuarioLogueado.getTaller() != null ? usuarioLogueado.getTaller().getId() : "null"));
        System.out.println("Taller pedido por URL: " + tallerId);

        // Validar que el usuario logueado tiene permiso para consultar ese taller
        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuarioLogueado.getTaller() == null || !usuarioLogueado.getTaller().getId().equals(tallerId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para ver estos usuarios");
        }

        List<Usuario> usuarios;

        if (rol != null && rol.equalsIgnoreCase("MECANICO")) {
            usuarios = usuarioRepository.findByTallerIdAndRol(tallerId, Usuario.Rol.MECANICO);
        } else {
            usuarios = usuarioRepository.findByTallerId(tallerId);
        }

        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDTO);
    }




    // Usuario -> DTO (sin password)
    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setEstado(u.getEstado());

        if (u.getTaller() != null) {
            dto.setTallerId(u.getTaller().getId());
            dto.setTallerNombre(u.getTaller().getNombre());
        }

        // Nuevos campos para mecánicos
        dto.setTelefono(u.getTelefono());
        dto.setEspecialidad(u.getEspecialidad());
        dto.setObservaciones(u.getObservaciones());
        dto.setActivo(u.getActivo());

        return dto;
    }

    // Extrae usuario logueado desde token
    private Usuario obtenerUsuarioLogueado(String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Listar todos los usuarios del taller del usuario logueado
    @GetMapping
    public ResponseEntity<?> listarUsuarios(@RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        List<Usuario> usuarios;

        if (usuarioLogueado.getRol() == Usuario.Rol.SUPERADMIN) {
            // El superadmin ve todos, pero no puede crear/eliminar otros superadmins (regla en POST/PUT/DELETE)
            usuarios = usuarioRepository.findAll();
        } else {
            usuarios = usuarioRepository.findByTaller(usuarioLogueado.getTaller());
        }

        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDTO);
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");

        Usuario usuario = optUsuario.get();
        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuario.getTaller() == null || !usuario.getTaller().equals(usuarioLogueado.getTaller()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso");
        }

        return ResponseEntity.ok(toDTO(usuario));
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO,
                                          @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");

        // Solo SUPERADMIN y ADMIN pueden crear usuarios (MECANICO no)
        if (usuarioLogueado.getRol() == Usuario.Rol.MECANICO)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");

        // Bloquear creación de SUPERADMIN por API, solo puede haber uno (vos)
        if (usuarioDTO.getRol() == Usuario.Rol.SUPERADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No se permite crear usuarios con rol SUPERADMIN");
        }

        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent())
            return ResponseEntity.badRequest().body("Email ya registrado");

        Usuario nuevoUsuario = new Usuario();

        nuevoUsuario.setEstado(Usuario.Estado.ACTIVO);

        fromDTO(usuarioDTO, nuevoUsuario);

        if (nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().isBlank())
            return ResponseEntity.badRequest().body("La contraseña es obligatoria");

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(nuevoUsuario));
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @RequestBody UsuarioDTO usuarioDTO,
                                               @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");

        // MECANICOS no pueden actualizar usuarios
        if (usuarioLogueado.getRol() == Usuario.Rol.MECANICO)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");

        Usuario usuarioExistente = optUsuario.get();

        // Controlar que si es SUPERADMIN no pueda ser modificado por otro (solo él mismo)
        if (usuarioExistente.getRol() == Usuario.Rol.SUPERADMIN) {
            // Solo superadmin puede modificarse a sí mismo
            if (!usuarioLogueado.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para modificar al SUPERADMIN");
            }
            // No permitir cambiar rol ni estado
            if (usuarioDTO.getRol() != Usuario.Rol.SUPERADMIN ||
                    usuarioDTO.getEstado() != usuarioExistente.getEstado()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se puede cambiar rol ni estado del SUPERADMIN");
            }
        } else {
            // Para otros usuarios:
            // Solo superadmin o admin del mismo taller pueden modificar
            if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                    (usuarioExistente.getTaller() == null || !usuarioExistente.getTaller().equals(usuarioLogueado.getTaller()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso");
            }

            // No permitir cambiar a SUPERADMIN en actualización
            if (usuarioDTO.getRol() == Usuario.Rol.SUPERADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No se permite asignar el rol SUPERADMIN");
            }
        }

        // Verificar que no se repita email (si se modificó)
        if (!usuarioExistente.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email ya registrado");
        }

        fromDTO(usuarioDTO, usuarioExistente);
        usuarioRepository.save(usuarioExistente);

        return ResponseEntity.ok(toDTO(usuarioExistente));
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);
        if (usuarioLogueado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");

        // MECANICOS no pueden eliminar usuarios
        if (usuarioLogueado.getRol() == Usuario.Rol.MECANICO)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");

        Usuario usuario = optUsuario.get();

        // No se puede eliminar al SUPERADMIN por API
        if (usuario.getRol() == Usuario.Rol.SUPERADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se puede eliminar al SUPERADMIN");
        }

        // Solo superadmin o admin del mismo taller pueden eliminar usuarios
        if (usuarioLogueado.getRol() != Usuario.Rol.SUPERADMIN &&
                (usuario.getTaller() == null || !usuario.getTaller().equals(usuarioLogueado.getTaller()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso");
        }

        usuarioRepository.delete(usuario);
        return ResponseEntity.ok("Usuario eliminado");
    }

//obtener datos del usuario logueado
    @GetMapping("/yo")
    public ResponseEntity<?> obtenerDatosUsuarioLogueado(@RequestHeader("Authorization") String token) {
        Usuario usuarioLogueado = obtenerUsuarioLogueado(token);  // el método privado sigue igual
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }
        return ResponseEntity.ok(toDTO(usuarioLogueado));
    }

}
