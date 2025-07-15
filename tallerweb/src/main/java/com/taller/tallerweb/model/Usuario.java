package com.taller.tallerweb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Rol {
        SUPERADMIN,
        ADMIN,
        MECANICO
    }

    public enum Estado {
        ACTIVO,
        SUSPENDIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "taller_id")
    @JsonBackReference
    private Taller taller; // puede ser null si es superadmin

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    // Nuevos atributos para mecánicos
    @Column(length = 20, nullable = true)
    private String telefono;

    @Column(length = 100, nullable = true)
    private String especialidad;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String observaciones;

    @Column(nullable = false)
    private Boolean activo = true;

    // Constructor vacío
    public Usuario() {}

    // Constructor completo (sin id), podés agregar los nuevos campos si querés
    public Usuario(String nombre, String email, String password, Rol rol, Estado estado, Taller taller,
                   String telefono, String especialidad, String observaciones, Boolean activo) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.taller = taller;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.observaciones = observaciones;
        this.activo = activo;
    }

    // Getters y setters existentes...

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Taller getTaller() {
        return taller;
    }

    public void setTaller(Taller taller) {
        this.taller = taller;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    // Getters y setters nuevos

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
