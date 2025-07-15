package com.taller.tallerweb;

import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TallerwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TallerwebApplication.class, args);
	}

	@Bean
	public CommandLineRunner crearUsuarioInicial(UsuarioRepository usuarioRepository) {
		return args -> {
			if (usuarioRepository.findByEmail("admin").isEmpty()) {
				String password = new BCryptPasswordEncoder().encode("123456");

				Usuario usuario = new Usuario();
				usuario.setNombre("Administrador");
				usuario.setEmail("admin");
				usuario.setPassword(password);
				usuario.setRol(Usuario.Rol.SUPERADMIN); // asegurate de importar esto
				usuario.setEstado(Usuario.Estado.ACTIVO);

				usuarioRepository.save(usuario);

				System.out.println("✅ Usuario admin creado con contraseña 123456");
			} else {
				System.out.println("ℹ️ Usuario admin ya existe");
			}
		};
	}
}
