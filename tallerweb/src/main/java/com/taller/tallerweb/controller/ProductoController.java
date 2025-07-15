package com.taller.tallerweb.controller;

import com.taller.tallerweb.model.Producto;
import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.model.Categoria;
import com.taller.tallerweb.repository.ProductoRepository;
import com.taller.tallerweb.repository.TallerRepository;
import com.taller.tallerweb.repository.CategoriaRepository;  // Asegúrate de tener este repo para Categoria

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallerRepository tallerRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @PostMapping
    public Producto createProducto(@RequestBody Producto producto) {
        // Validar Taller
        if (producto.getTaller() != null && producto.getTaller().getId() != null) {
            Taller tallerExistente = tallerRepository.findById(producto.getTaller().getId())
                    .orElseThrow(() -> new RuntimeException("Taller no encontrado"));
            producto.setTaller(tallerExistente);
        } else {
            throw new RuntimeException("Taller es obligatorio");
        }

        // Validar Categoria
        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            Categoria categoriaExistente = categoriaRepository.findById(producto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoriaExistente);
        } else {
            throw new RuntimeException("Categoría es obligatoria");
        }

        return productoRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setCodigo(productoDetails.getCodigo());
        producto.setNombre(productoDetails.getNombre());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setPrecioCompra(productoDetails.getPrecioCompra());
        producto.setPrecioVenta(productoDetails.getPrecioVenta());
        producto.setCantidad(productoDetails.getCantidad());

        // Actualizar Taller si viene en la petición
        if (productoDetails.getTaller() != null && productoDetails.getTaller().getId() != null) {
            Taller tallerExistente = tallerRepository.findById(productoDetails.getTaller().getId())
                    .orElseThrow(() -> new RuntimeException("Taller no encontrado"));
            producto.setTaller(tallerExistente);
        }

        // Actualizar Categoria si viene en la petición
        if (productoDetails.getCategoria() != null && productoDetails.getCategoria().getId() != null) {
            Categoria categoriaExistente = categoriaRepository.findById(productoDetails.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoriaExistente);
        }

        return productoRepository.save(producto);
    }

    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }

    @GetMapping("/taller/{tallerId}")
    public List<Producto> getProductosPorTaller(@PathVariable Long tallerId) {
        return productoRepository.findByTallerId(tallerId);
    }
}
