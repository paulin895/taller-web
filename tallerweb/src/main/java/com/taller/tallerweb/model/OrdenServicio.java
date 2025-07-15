package com.taller.tallerweb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ordenes")
public class OrdenServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Usuario mecanico;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    @Column(name = "kilometraje")
    private Integer kilometraje;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "descripcion_cierre", columnDefinition = "TEXT")
    private String descripcionCierre;

    @Column(name = "condicion_final", length = 100)
    private String condicionFinal;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "nota_interna", columnDefinition = "TEXT")
    private String notaInterna;

    // Getters y setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Taller getTaller() {
        return taller;
    }
    public void setTaller(Taller taller) {
        this.taller = taller;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }
    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Usuario getMecanico() {
        return mecanico;
    }
    public void setMecanico(Usuario mecanico) {
        this.mecanico = mecanico;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }
    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public EstadoOrden getEstado() {
        return estado;
    }
    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }
    public void setKilometraje(Integer kilometraje) {
        this.kilometraje = kilometraje;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }
    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getDescripcionCierre() {
        return descripcionCierre;
    }
    public void setDescripcionCierre(String descripcionCierre) {
        this.descripcionCierre = descripcionCierre;
    }

    public String getCondicionFinal() {
        return condicionFinal;
    }
    public void setCondicionFinal(String condicionFinal) {
        this.condicionFinal = condicionFinal;
    }

    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getNotaInterna() {
        return notaInterna;
    }
    public void setNotaInterna(String notaInterna) {
        this.notaInterna = notaInterna;
    }

    // Enum
    public enum EstadoOrden {
        PENDIENTE,
        EN_PROCESO,
        FINALIZADA,
        CERRADA

    }
}
