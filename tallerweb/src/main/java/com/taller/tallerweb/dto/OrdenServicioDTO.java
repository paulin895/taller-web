package com.taller.tallerweb.dto;

import com.taller.tallerweb.model.OrdenServicio.EstadoOrden;
import java.time.LocalDate;

public class OrdenServicioDTO {

    private Long id;
    private Long vehiculoId;
    private Long mecanicoId;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String descripcion;
    private String observaciones;
    private EstadoOrden estado;
    private Integer kilometraje;
    private Double precio;
    private String notaInterna;
    private String condicionFinal;
    private String descripcionCierre;

    // Para mostrar en la tabla
    private String clienteNombreCompleto;
    private String vehiculoDescripcion;
    private String mecanicoNombre;

    // 👉 Extras usados por el controlador
    private String vehiculoMarca;
    private String vehiculoModelo;
    private String vehiculoMatricula;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }

    public Long getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Long mecanicoId) { this.mecanicoId = mecanicoId; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getNotaInterna() { return notaInterna; }
    public void setNotaInterna(String notaInterna) { this.notaInterna = notaInterna; }

    public String getCondicionFinal() { return condicionFinal; }
    public void setCondicionFinal(String condicionFinal) { this.condicionFinal = condicionFinal; }

    public String getClienteNombreCompleto() { return clienteNombreCompleto; }
    public void setClienteNombreCompleto(String clienteNombreCompleto) { this.clienteNombreCompleto = clienteNombreCompleto; }

    public String getVehiculoDescripcion() { return vehiculoDescripcion; }
    public void setVehiculoDescripcion(String vehiculoDescripcion) { this.vehiculoDescripcion = vehiculoDescripcion; }

    public String getMecanicoNombre() { return mecanicoNombre; }
    public void setMecanicoNombre(String mecanicoNombre) { this.mecanicoNombre = mecanicoNombre; }

    public String getVehiculoMarca() { return vehiculoMarca; }
    public void setVehiculoMarca(String vehiculoMarca) { this.vehiculoMarca = vehiculoMarca; }

    public String getVehiculoModelo() { return vehiculoModelo; }
    public void setVehiculoModelo(String vehiculoModelo) { this.vehiculoModelo = vehiculoModelo; }

    public String getVehiculoMatricula() { return vehiculoMatricula; }
    public void setVehiculoMatricula(String vehiculoMatricula) { this.vehiculoMatricula = vehiculoMatricula; }

    public String getDescripcionCierre() {
        return descripcionCierre;
    }

    public void setDescripcionCierre(String descripcionCierre) {
        this.descripcionCierre = descripcionCierre;
    }

    // Alias extra por compatibilidad con controlador viejo
    public void setClienteNombre(String clienteNombreCompleto) {
        this.clienteNombreCompleto = clienteNombreCompleto;
    }
}
