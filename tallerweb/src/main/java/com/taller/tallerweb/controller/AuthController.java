package com.taller.tallerweb.controller;

import com.taller.tallerweb.dto.AuthRequest;
import com.taller.tallerweb.dto.AuthResponse;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.UsuarioRepository;
import com.taller.tallerweb.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        System.out.println("Intento de login - Usuario: " + authRequest.getEmail() + " Password: " + authRequest.getPassword());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            System.out.println("Error de credenciales: " + e.getMessage());
            return ResponseEntity.status(401).body(new AuthResponse("Credenciales inválidas"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(authRequest.getEmail());
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuario no encontrado en base de datos");
            return ResponseEntity.status(401).body(new AuthResponse("Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        // Asignar rol con prefijo ROLE_ para que funcione en Spring Security
        List<String> roles = List.of("ROLE_" + usuario.getRol().name());

        System.out.println("Roles que se están agregando al token: " + roles);

        //String token = jwtUtil.generateToken(usuario.getEmail(), roles);
        String token = jwtUtil.generateToken(usuario.getEmail(), roles, usuario.getTaller() != null ? usuario.getTaller().getId() : null);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
