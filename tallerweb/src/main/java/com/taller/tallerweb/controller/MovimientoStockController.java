package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.MovimientoStock;
import com.taller.tallerweb.repository.MovimientoStockRepository;
import com.taller.tallerweb.repository.TallerRepository;
import com.taller.tallerweb.model.Taller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoStockController {

    @Autowired
    private MovimientoStockRepository movimientoStockRepository;

    @Autowired
    private TallerRepository tallerRepository;

    @GetMapping
    public List<MovimientoStock> getAllMovimientos() {
        return movimientoStockRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<MovimientoStock> getMovimientoById(@PathVariable Long id) {
        return movimientoStockRepository.findById(id);
    }

    @PostMapping
    public MovimientoStock createMovimiento(@RequestBody MovimientoStock movimiento) {
        if (movimiento.getTaller() != null) {
            Long idTaller = movimiento.getTaller().getId();
            Taller tallerExistente = tallerRepository.findById(idTaller)
                    .orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + idTaller));
            movimiento.setTaller(tallerExistente);
        }
        return movimientoStockRepository.save(movimiento);
    }

    @PutMapping("/{id}")
    public MovimientoStock updateMovimiento(@PathVariable Long id, @RequestBody MovimientoStock movimientoDetails) {
        MovimientoStock movimiento = movimientoStockRepository.findById(id).orElseThrow();

        movimiento.setProducto(movimientoDetails.getProducto());
        movimiento.setTipo(movimientoDetails.getTipo());
        movimiento.setCantidad(movimientoDetails.getCantidad());
        movimiento.setFecha(movimientoDetails.getFecha());
        movimiento.setMotivo(movimientoDetails.getMotivo());

        return movimientoStockRepository.save(movimiento);
    }

    @DeleteMapping("/{id}")
    public void deleteMovimiento(@PathVariable Long id) {
        movimientoStockRepository.deleteById(id);
    }

    @GetMapping("/taller/{tallerId}")
    public List<MovimientoStock> getMovimientosPorTaller(@PathVariable Long tallerId) {
        return movimientoStockRepository.findByTallerId(tallerId);
    }
}
