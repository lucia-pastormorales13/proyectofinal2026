package com.Bumeran.Prestamos.dto;

import lombok.Data;

@Data
public class CrearArticuloRequest {
    private String nombre;
    private String descripcion;
    private Long propietarioId;
}