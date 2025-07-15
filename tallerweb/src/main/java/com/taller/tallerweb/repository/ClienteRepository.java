package com.taller.tallerweb.repository;

import com.taller.tallerweb.model.Cliente;
import com.taller.tallerweb.model.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);

    List<Cliente> findByTaller(Taller taller);

    List<Cliente> findByTallerId(Long tallerId);


    List<Cliente> findByTallerAndNombreCompletoContainingIgnoreCase(Taller taller, String nombreCompleto);
}
