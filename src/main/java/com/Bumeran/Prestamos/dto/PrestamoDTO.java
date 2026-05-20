package com.Bumeran.Prestamos.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PrestamoDTO {
    private Long id;
    private String nombreArticulo;
    private String nombreReceptor; // Aquí unificas el texto manual o el nombre del usuario registrado
    private LocalDate fechaInicio;
    private long diasPrestado;
    private String estado;
}
//dto le envia al frontend la informacion de manera unificada, para que el frontend no tenga que preocuparse por si el receptor es un usuario registrado o no, y para mostrar el estado del préstamo de forma clara.