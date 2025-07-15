package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.service.TallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/talleres")
@CrossOrigin(origins = "*") // Podés restringir según el dominio real
public class TallerController {


    private static final Logger log = LoggerFactory.getLogger(TallerController.class);
    @Autowired
    private TallerService tallerService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Taller crearTaller(@RequestBody Taller taller) {

        System.out.println("ROL desde contexto: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        taller.setFechaRegistro(LocalDate.now());
        return tallerService.guardar(taller);
    }

    @GetMapping
    public List<Taller> listarTalleres() {
        return tallerService.listarTodos();
    }

    @GetMapping("/{id}")
    public Taller obtenerTallerPorId(@PathVariable Long id) {
        return tallerService.buscarPorId(id).orElse(null);
    }


    @PreAuthorize("hasRole('SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTaller(@PathVariable Long id, @RequestBody Taller tallerActualizado) {
        System.out.println("ROL desde contexto: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        Optional<Taller> optionalTaller = tallerService.buscarPorId(id);

        if (optionalTaller.isPresent()) {
            Taller t = optionalTaller.get();
            t.setNombre(tallerActualizado.getNombre());
            t.setDireccion(tallerActualizado.getDireccion());
            t.setTelefono(tallerActualizado.getTelefono());
            t.setCorreo(tallerActualizado.getCorreo());
            t.setEstado(tallerActualizado.getEstado());
            t.setPlan(tallerActualizado.getPlan());
            Taller tallerGuardado = tallerService.guardar(t);
            return ResponseEntity.ok(tallerGuardado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Taller no encontrado");
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/{id}")
    public void eliminarTaller(@PathVariable Long id) {
        tallerService.eliminar(id);
    }
}
