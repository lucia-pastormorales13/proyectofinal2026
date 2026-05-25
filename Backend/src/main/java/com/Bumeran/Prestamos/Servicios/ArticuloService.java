package com.Bumeran.Prestamos.Servicios; // Asegúrate de que coincida con tu paquete

import java.util.List;

import org.springframework.stereotype.Service;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Repositories.ArticuloRepository;

@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;

    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo registrarArticulo(Articulo articulo) {
        // Por defecto, cuando lo registras, lo tienes tú
        articulo.setEstado("DISPONIBLE");
        return articuloRepository.save(articulo);
    }

    public List<Articulo> obtenerMisArticulos(Long propietarioId) {
        return articuloRepository.findByPropietarioId(propietarioId);
    }
}