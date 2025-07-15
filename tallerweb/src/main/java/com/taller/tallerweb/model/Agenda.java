package com.taller.tallerweb.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import com.taller.tallerweb.model.EstadoAgenda;

@Entity
@Table(name = "agendas")
public class Agenda {

    @ManyToOne
    @JoinColumn(name = "taller_id")
    private Taller taller;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalTime hora;

    private String motivo;
    private String observaciones;

    @Enumerated(EnumType.STRING)
    private EstadoAgenda estado;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Vehiculo vehiculo;


    // 🔹 Constructor vacío
    public Agenda() {}

    // 🔹 Constructor con campos
    public Agenda(LocalDate fecha, LocalTime hora, String motivo, String observaciones, EstadoAgenda estado, Cliente cliente, Vehiculo vehiculo) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.observaciones = observaciones;
        this.estado = estado;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
    }

    // 🔹 Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public EstadoAgenda getEstado() { return estado; }
    public void setEstado(EstadoAgenda estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }


}
