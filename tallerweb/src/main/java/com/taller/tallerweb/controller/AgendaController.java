package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.Agenda;
import com.taller.tallerweb.model.EstadoAgenda;
import com.taller.tallerweb.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    @Autowired
    private AgendaRepository agendaRepository;

    @GetMapping
    public List<Agenda> getAllAgenda() {
        return agendaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Agenda> getAgendaById(@PathVariable Long id) {
        return agendaRepository.findById(id);
    }

    @PostMapping
    public Agenda createAgenda(@RequestBody Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agenda> updateAgenda(@PathVariable Long id, @RequestBody Agenda agendaDetails) {
        Optional<Agenda> optionalAgenda = agendaRepository.findById(id);

        if (optionalAgenda.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Agenda agenda = optionalAgenda.get();
        agenda.setFecha(agendaDetails.getFecha());
        agenda.setHora(agendaDetails.getHora());
        agenda.setMotivo(agendaDetails.getMotivo());
        agenda.setObservaciones(agendaDetails.getObservaciones());
        agenda.setEstado(agendaDetails.getEstado());
        agenda.setCliente(agendaDetails.getCliente());
        agenda.setVehiculo(agendaDetails.getVehiculo());


        return ResponseEntity.ok(agendaRepository.save(agenda));
    }

    @DeleteMapping("/{id}")
    public void deleteAgenda(@PathVariable Long id) {
        agendaRepository.deleteById(id);
    }
}
