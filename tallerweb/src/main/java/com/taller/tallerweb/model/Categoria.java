package com.taller.tallerweb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias")  // Nombre plural que definiste en la BD
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)  // No puede ser null y es único
    private String nombre;

    @Column(nullable = true)
    private String descripcion;

    @ManyToOne(optional = false)  // Obligatorio que tenga un taller asociado
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @OneToMany(mappedBy = "categoria")
    //@JsonManagedReference
    private List<Producto> productos;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Taller getTaller() {
        return taller;
    }

    public void setTaller(Taller taller) {
        this.taller = taller;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
