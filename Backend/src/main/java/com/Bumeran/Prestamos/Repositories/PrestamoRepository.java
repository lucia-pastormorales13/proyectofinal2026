package com.Bumeran.Prestamos.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bumeran.Prestamos.Entidades.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    
    // Busca los préstamos que yo he creado (como dueño del objeto)
    List<Prestamo> findByUsuarioPropietarioId(Long usuarioPropietarioId);

    // Busca los préstamos de un artículo específico
    List<Prestamo> findByArticuloId(Long articuloId);
    
    // Busca lo que le he prestado a un amigo registrado
    List<Prestamo> findByUsuarioReceptorId(Long usuarioReceptorId);
}