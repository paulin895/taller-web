package com.taller.tallerweb.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "talleres")
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 150)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoTaller estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PlanTaller plan;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "fecha_ultimo_pago")
    private LocalDate fechaUltimoPago;

    // 🔹 Relaciones

    @OneToMany(mappedBy = "taller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Usuario> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "taller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cliente> clientes = new ArrayList<>();

    @OneToMany(mappedBy = "taller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @OneToMany(mappedBy = "taller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrdenServicio> ordenesServicio = new ArrayList<>();

    // 🔹 Constructor vacío
    public Taller() {}

    // 🔹 Getters y Setters

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public EstadoTaller getEstado() {
        return estado;
    }

    public void setEstado(EstadoTaller estado) {
        this.estado = estado;
    }

    public PlanTaller getPlan() {
        return plan;
    }

    public void setPlan(PlanTaller plan) {
        this.plan = plan;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaUltimoPago() {
        return fechaUltimoPago;
    }

    public void setFechaUltimoPago(LocalDate fechaUltimoPago) {
        this.fechaUltimoPago = fechaUltimoPago;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public List<OrdenServicio> getOrdenesServicio() {
        return ordenesServicio;
    }

    public void setOrdenesServicio(List<OrdenServicio> ordenesServicio) {
        this.ordenesServicio = ordenesServicio;
    }
}
