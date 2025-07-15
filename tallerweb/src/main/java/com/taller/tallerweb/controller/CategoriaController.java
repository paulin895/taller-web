package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.Categoria;
import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.repository.CategoriaRepository;
import com.taller.tallerweb.repository.TallerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TallerRepository tallerRepository;

    // Obtener todas las categorías
    @GetMapping
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public Optional<Categoria> getCategoriaById(@PathVariable Long id) {
        return categoriaRepository.findById(id);
    }

    // Crear una nueva categoría
    @PostMapping
    public ResponseEntity<?> createCategoria(@RequestBody Categoria categoria) {
        try {
            if (categoria.getTaller() != null) {
                Long idTaller = categoria.getTaller().getId();
                Taller tallerExistente = tallerRepository.findById(idTaller)
                        .orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + idTaller));
                categoria.setTaller(tallerExistente);
            }
            Categoria nueva = categoriaRepository.save(categoria);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al guardar la categoría");
        }
    }

    // Actualizar una categoría existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategoria(@PathVariable Long id, @RequestBody Categoria categoriaDetails) {
        try {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

            categoria.setNombre(categoriaDetails.getNombre());
            categoria.setDescripcion(categoriaDetails.getDescripcion());

            return ResponseEntity.ok(categoriaRepository.save(categoria));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la categoría");
        }
    }

    // Eliminar una categoría si no tiene productos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(id);

        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Categoria categoria = categoriaOpt.get();
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la categoría porque tiene productos asociados.");
        }

        categoriaRepository.deleteById(id);
        return ResponseEntity.ok("Categoría eliminada correctamente.");
    }

    // Obtener categorías por taller
    @GetMapping("/taller/{tallerId}")
    public List<Categoria> getCategoriasPorTaller(@PathVariable Long tallerId) {
        System.out.println("Buscando categorías para taller ID: " + tallerId);
        return categoriaRepository.findByTallerId(tallerId);
    }
}
