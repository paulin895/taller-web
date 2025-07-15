package com.taller.tallerweb.repository;

import com.taller.tallerweb.model.OrdenServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {

    // Trae todas las órdenes del taller (para admin) con vehiculo, cliente, mecanico y taller
    @Query("SELECT o FROM OrdenServicio o " +
            "JOIN FETCH o.vehiculo v " +
            "JOIN FETCH v.cliente c " +
            "JOIN FETCH o.mecanico m " +
            "JOIN FETCH o.taller t " +
            "WHERE t.id = :tallerId")
    List<OrdenServicio> findByTallerId(@Param("tallerId") Long tallerId);

    // Trae órdenes del taller filtrando por mecánico (para mecánicos) con vehiculo, cliente, mecanico y taller
    @Query("SELECT o FROM OrdenServicio o " +
            "JOIN FETCH o.vehiculo v " +
            "JOIN FETCH v.cliente c " +
            "JOIN FETCH o.mecanico m " +
            "JOIN FETCH o.taller t " +
            "WHERE t.id = :tallerId AND m.id = :mecanicoId")
    List<OrdenServicio> findByTallerIdAndMecanicoId(@Param("tallerId") Long tallerId,
                                                    @Param("mecanicoId") Long mecanicoId);
}
