package com.taller.tallerweb.mapper;

import com.taller.tallerweb.dto.OrdenServicioDTO;
import com.taller.tallerweb.model.OrdenServicio;
import com.taller.tallerweb.model.Usuario;
import com.taller.tallerweb.model.Vehiculo;

import java.util.List;
import java.util.stream.Collectors;

public class OrdenServicioMapper {

    public static OrdenServicioDTO toDTO(OrdenServicio orden) {
        OrdenServicioDTO dto = new OrdenServicioDTO();

        dto.setId(orden.getId());
        dto.setVehiculoId(orden.getVehiculo().getId());
        dto.setMecanicoId(orden.getMecanico().getId());
        dto.setFechaEntrada(orden.getFechaEntrada());
        dto.setFechaSalida(orden.getFechaSalida());
        dto.setDescripcion(orden.getDescripcion());
        dto.setObservaciones(orden.getObservaciones());
        dto.setEstado(orden.getEstado());
        dto.setKilometraje(orden.getKilometraje());
        dto.setPrecio(orden.getPrecio());
        dto.setNotaInterna(orden.getNotaInterna());
        dto.setCondicionFinal(orden.getCondicionFinal());

        Vehiculo v = orden.getVehiculo();
        if (v != null && v.getCliente() != null) {
            dto.setClienteNombreCompleto(v.getCliente().getNombreCompleto());
        } else {
            dto.setClienteNombreCompleto("Sin cliente");
        }

        if (v != null) {
            dto.setVehiculoDescripcion(v.getMarca() + " " + v.getModelo() + " - " + v.getMatricula());
        }

        Usuario m = orden.getMecanico();
        if (m != null) {
            dto.setMecanicoNombre(m.getNombre());
        }

        return dto;
    }

    public static List<OrdenServicioDTO> toDTOList(List<OrdenServicio> ordenes) {
        return ordenes.stream()
                .map(OrdenServicioMapper::toDTO)
                .collect(Collectors.toList());
    }
}
