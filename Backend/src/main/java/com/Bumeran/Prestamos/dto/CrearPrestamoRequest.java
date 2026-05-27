package com.Bumeran.Prestamos.dto;

import lombok.Data;

@Data
public class CrearPrestamoRequest {

    private Long articuloId;

    private Long usuarioReceptorId;

    private String nombreReceptor;
}