package com.Bumeran.Prestamos.Servicios;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Entidades.Prestamo;
import com.Bumeran.Prestamos.Repositories.ArticuloRepository;
import com.Bumeran.Prestamos.Repositories.PrestamoRepository;

import jakarta.transaction.Transactional;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final ArticuloRepository articuloRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, ArticuloRepository articuloRepository) {
        this.prestamoRepository = prestamoRepository;
        this.articuloRepository = articuloRepository;
    }

    @Transactional
    public Prestamo prestarObjeto(Prestamo prestamo) {
        // 1. Buscamos el artículo en la base de datos
        Articulo articulo = articuloRepository.findById(prestamo.getArticulo().getId())
                .orElseThrow(() -> new RuntimeException("El artículo no existe"));

        // 2. Validamos que no esté ya prestado
        if ("PRESTADO".equalsIgnoreCase(articulo.getEstado())) {
            throw new IllegalStateException("¡Error! Este artículo ya está prestado a alguien.");
        }

        // 3. Cambiamos el estado del artículo
        articulo.setEstado("PRESTADO");
        articuloRepository.save(articulo);

        // 4. Configuramos la fecha de inicio hoy y guardamos el préstamo
        prestamo.setFechaInicio(LocalDate.now());
        
        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public Prestamo devolverObjeto(Long prestamoId) {
        // 1. Buscamos el préstamo
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getFechaDevuelto() != null) {
            throw new IllegalStateException("Este objeto ya fue devuelto anteriormente.");
        }

        // 2. Marcamos la fecha de devolución
        prestamo.setFechaDevuelto(LocalDate.now());

        // 3. Volvemos a poner el artículo como disponible
        Articulo articulo = prestamo.getArticulo();
        articulo.setEstado("DISPONIBLE");
        articuloRepository.save(articulo);

        // 4. Guardamos los cambios
        return prestamoRepository.save(prestamo);
    }
}
