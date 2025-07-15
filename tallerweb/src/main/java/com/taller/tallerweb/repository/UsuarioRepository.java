package com.taller.tallerweb.repository;
import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.model.Usuario.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByTallerId(Long tallerId);

    List<Usuario> findByTallerIdAndRol(Long tallerId, Rol rol);

    List<Usuario> findByTaller(Taller taller);  // <-- Agregar este método
}
