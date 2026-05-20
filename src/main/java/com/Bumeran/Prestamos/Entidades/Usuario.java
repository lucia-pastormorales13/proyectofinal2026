package com.Bumeran.Prestamos.Entidades;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data   //crea getters y setters automáticamente, entre otras cosas
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "usuarios")   
public class Usuario {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String nombre;
    
    @Column (nullable = false, unique = true)
    private String email;

    @Column (nullable = false)
    private String password;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<Articulo> misArticulos;

    // Los préstamos donde este usuario es el que RECIBE el objeto
    @OneToMany(mappedBy = "usuarioReceptor")
    @JsonIgnore 
    private List<Prestamo> objetosQueTengoPrestados;
}