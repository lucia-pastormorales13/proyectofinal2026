package com.Bumeran.Prestamos.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bumeran.Prestamos.Entidades.Articulo;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    List<Articulo> findByPropietarioId(Long propietarioId);
}