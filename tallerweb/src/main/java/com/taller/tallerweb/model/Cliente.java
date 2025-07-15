// Paquete donde se encuentra la clase
package com.taller.tallerweb.model;

// Importaciones necesarias para JPA (persistencia) y manejo de fechas
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

//////////////////////////////////////////////////////////
// Entidad que representa un cliente en la base de datos
//////////////////////////////////////////////////////////
@Entity // Indica que esta clase es una entidad JPA y se mapeará a una tabla
@Table(name = "clientes") // Define el nombre de la tabla en la base de datos
public class Cliente {

    // Campo clave primaria, con generación automática (auto-incremental)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre completo del cliente, campo obligatorio, máximo 100 caracteres
    @Column(nullable = false, length = 100)
    private String nombreCompleto;

    // Cédula de identidad (CI) del cliente, máximo 12 caracteres
    @Column(length = 12)
    private String ci;

    // Teléfono del cliente, máximo 20 caracteres
    @Column(length = 20)
    private String telefono;

    // Correo electrónico del cliente, máximo 100 caracteres
    @Column(length = 100)
    private String correo;

    // Dirección física del cliente, máximo 150 caracteres
    @Column(length = 150)
    private String direccion;

    // Observaciones adicionales, anotado como @Lob por si se desea guardar textos largos
    @Lob
    private String observaciones;

    // Fecha y hora de registro del cliente. Se genera automáticamente al crear el registro.
    @Column(name = "fecha_registro", updatable = false) // No se puede actualizar luego de crearse
    @CreationTimestamp // Hibernate la genera automáticamente al momento de persistir el cliente
    private LocalDateTime fechaRegistro;

    // Relación muchos-a-uno: muchos clientes pueden pertenecer a un mismo taller
    @ManyToOne
    @JoinColumn(name = "taller_id", nullable = false) // Clave foránea a la tabla de talleres
    private Taller taller;

    //////////////////////////////////////////////////
    // Constructor por defecto (requerido por JPA)
    //////////////////////////////////////////////////
    public Cliente() {}

    ////////////////////////
    // Getters y Setters
    ////////////////////////

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // Este campo solo tiene getter, porque no debe modificarse manualmente
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public Taller getTaller() {
        return taller;
    }

    public void setTaller(Taller taller) {
        this.taller = taller;
    }
}
