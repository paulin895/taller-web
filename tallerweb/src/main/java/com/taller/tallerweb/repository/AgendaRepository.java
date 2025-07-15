package com.taller.tallerweb.repository;

import com.taller.tallerweb.model.Agenda;
import com.taller.tallerweb.model.EstadoAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByTaller_Id(Long tallerId);           // ✅
     // ✅

    List<Agenda> findByCliente_Id(Long clienteId);         // ✅

    List<Agenda> findByFecha(LocalDate fecha);             // ✅

    List<Agenda> findByEstado(EstadoAgenda estado);        // ✅

    List<Agenda> findByTaller_IdAndFecha(Long tallerId, LocalDate fecha); // ✅

    boolean existsByFechaAndHora(LocalDate fecha, java.time.LocalTime hora); // ✅
}
