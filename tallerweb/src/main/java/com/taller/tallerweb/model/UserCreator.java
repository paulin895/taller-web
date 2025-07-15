package com.taller.tallerweb;

import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserCreator {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @EventListener(ContextRefreshedEvent.class)
    public void createUser() {
        if (usuarioRepository.findByEmail("nuevo@taller.com").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setEmail("nuevo@taller.com");
            usuario.setPassword(passwordEncoder.encode("123456")); // Encriptar contraseña
            usuario.setNombre("Usuario Nuevo");
            usuario.setRol(Usuario.Rol.ADMIN);
            usuario.setEstado(Usuario.Estado.ACTIVO);
            // si quieres, también podés setear taller con usuario.setTaller(...)
            usuarioRepository.save(usuario);
            System.out.println("Usuario creado: " + usuario.getEmail());
        } else {
            System.out.println("Usuario ya existe.");
        }
    }

}
