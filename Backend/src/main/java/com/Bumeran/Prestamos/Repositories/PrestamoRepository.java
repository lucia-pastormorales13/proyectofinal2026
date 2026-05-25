package com.Bumeran.Prestamos.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bumeran.Prestamos.Entidades.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    // Historial de una cosa que has prestado
    List<Prestamo> findByArticuloId(Long articuloId);
    
    // Lo que le has prestado a un amigo con cuenta
    List<Prestamo> findByUsuarioReceptorId(Long usuarioReceptorId);
}
