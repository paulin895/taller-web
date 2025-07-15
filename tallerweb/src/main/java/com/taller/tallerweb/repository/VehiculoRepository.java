package com.taller.tallerweb.repository;

import com.taller.tallerweb.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByClienteId(Long clienteId);
    List<Vehiculo> findByTallerId(Long tallerId);
}
