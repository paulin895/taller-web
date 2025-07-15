package com.taller.tallerweb.service;

import com.taller.tallerweb.dto.UsuarioDTO;
import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.repository.TallerRepository;
import com.taller.tallerweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Marca esta clase como un componente de servicio de Spring
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para interactuar con la tabla de usuarios

    @Autowired
    private TallerRepository tallerRepository; // Repositorio para interactuar con la tabla de talleres

    /**
     * Método para crear un Usuario a partir de un DTO
     * @param dto El objeto recibido con los datos del usuario
     * @return el usuario creado y guardado en la base de datos
     */
    public Usuario crearUsuarioDesdeDTO(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());                // Establece el email
        usuario.setPassword(dto.getPassword());      // Establece la contraseña

        // Convierte el string a Enum Rol (en mayúsculas)
        try {
            usuario.setRol(dto.getRol());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + dto.getRol());
        }

        // Convierte el string a Enum Estado (en mayúsculas)
        try {
            usuario.setEstado(dto.getEstado());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + dto.getEstado());
        }

        // Asocia el taller si se especifica uno
        if (dto.getTallerId() != null) {
            Taller taller = tallerRepository.findById(dto.getTallerId())
                    .orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + dto.getTallerId()));
            usuario.setTaller(taller);
        } else {
            usuario.setTaller(null); // Si es superadmin
        }

        // Guarda el usuario en la base de datos y lo devuelve
        return usuarioRepository.save(usuario);
    }


}
