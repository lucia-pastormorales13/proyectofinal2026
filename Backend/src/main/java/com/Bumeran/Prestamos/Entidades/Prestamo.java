package com.Bumeran.Prestamos.Entidades;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.Bumeran.Prestamos.Enummeration.EstadoPrestamo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    // OPCIÓN A: Receptor no registrado (texto)
    @Column(name = "nombre_receptor")
    private String nombreReceptor;

    // OPCIÓN B: Receptor registrado en la app
    @ManyToOne
    @JoinColumn(name = "usuario_receptor_id")
    private Usuario usuarioReceptor;

    // Este campo guardará al usuario que está prestando el objeto
    @ManyToOne
    @JoinColumn(name = "usuario_propietario_id")
    private Usuario usuarioPropietario;
    
    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaDevuelto;

    // Este campo no se guarda en la base de datos, se calcula al vuelo
    @Transient
    public long getDiasPrestado() {
        if (fechaDevuelto != null) {
            return ChronoUnit.DAYS.between(fechaInicio, fechaDevuelto);
        }
        return ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
    }

    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado = EstadoPrestamo.EN_CURSO;

    
}
