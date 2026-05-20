package com.Bumeran.Prestamos.Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "articulos")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    // Podría ser un String o un Enum (DISPONIBLE, PRESTADO, PERDIDO)
    @Column(nullable = false)
    private String estado = "DISPONIBLE";

    // El dueño del artículo
    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
 

   
}
