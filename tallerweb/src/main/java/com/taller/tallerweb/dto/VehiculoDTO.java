package com.taller.tallerweb.dto;

public class VehiculoDTO {
    private Long id;
    private String marca;
    private String modelo;
    private int anio;
    private String matricula;
    private String color;
    private String nombreCliente;

    // Constructor
    public VehiculoDTO(Long id, String marca, String modelo, int anio, String matricula, String color, String nombreCliente) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.matricula = matricula;
        this.color = color;
        this.nombreCliente = nombreCliente;
    }

    // Getters
    public Long getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnio() { return anio; }
    public String getMatricula() { return matricula; }
    public String getColor() { return color; }
    public String getNombreCliente() { return nombreCliente; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public void setColor(String color) { this.color = color; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}
