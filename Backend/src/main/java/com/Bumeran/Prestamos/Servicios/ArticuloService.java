package com.Bumeran.Prestamos.Servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Repositories.ArticuloRepository;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;
import com.Bumeran.Prestamos.dto.CrearArticuloRequest;

@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final UsuarioRepository usuarioRepository;

    public ArticuloService(ArticuloRepository articuloRepository, UsuarioRepository usuarioRepository) {
        this.articuloRepository = articuloRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Articulo registrarArticulo(CrearArticuloRequest request) {
        Usuario propietario = usuarioRepository.findById(request.getPropietarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Articulo articulo = new Articulo();
        articulo.setNombre(request.getNombre());
        articulo.setDescripcion(request.getDescripcion());
        articulo.setEstado("DISPONIBLE");
        articulo.setPropietario(propietario);

        return articuloRepository.save(articulo);
    }

    public List<Articulo> obtenerMisArticulos(Long propietarioId) {
        return articuloRepository.findByPropietarioId(propietarioId);
    }

    public void eliminarArticulo(Long id) {
        if (!articuloRepository.existsById(id)) {
            throw new RuntimeException("Artículo no encontrado");
        }
        articuloRepository.deleteById(id);
    }
}