package com.taller.tallerweb.dto;

import com.taller.tallerweb.model.Usuario;

public class UsuarioDTO {

    private String nombre;
    private String password;
    private String email;
    private Usuario.Rol rol;
    private Usuario.Estado estado;
    private Long tallerId;
    private String tallerNombre;  // Puede ser null si es superadmin
    private Long id;

    // Nuevos campos mecánicos
    private String telefono;
    private String especialidad;
    private String observaciones;
    private Boolean activo;

    public UsuarioDTO() {}

    // Getters y setters existentes

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario.Rol getRol() {
        return rol;
    }

    public void setRol(Usuario.Rol rol) {
        this.rol = rol;
    }

    public Usuario.Estado getEstado() {
        return estado;
    }

    public void setEstado(Usuario.Estado estado) {
        this.estado = estado;
    }

    public Long getTallerId() {
        return tallerId;
    }

    public void setTallerId(Long tallerId) {
        this.tallerId = tallerId;
    }

    public String getTallerNombre() {
        return tallerNombre;
    }

    public void setTallerNombre(String tallerNombre) {
        this.tallerNombre = tallerNombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
