package com.Bumeran.Prestamos.Servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Entidades.Prestamo;
import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Enummeration.EstadoPrestamo;
import com.Bumeran.Prestamos.Repositories.ArticuloRepository;
import com.Bumeran.Prestamos.Repositories.PrestamoRepository;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;
import com.Bumeran.Prestamos.dto.CrearPrestamoRequest;

@Service
public class PrestamoService {

    @Autowired private PrestamoRepository prestamoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ArticuloRepository articuloRepository;

    public List<Prestamo> listarMisPrestamos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email);
        return prestamoRepository.findByUsuarioPropietarioId(usuario.getId());
    }

    public Prestamo prestarObjeto(CrearPrestamoRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario propietario = usuarioRepository.findByEmail(email);

        Articulo articulo = articuloRepository.findById(request.getArticuloId())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        if (!articulo.getEstado().equals("DISPONIBLE")) {
            throw new RuntimeException("El artículo no está disponible");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setArticulo(articulo);
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.EN_CURSO);
        prestamo.setUsuarioPropietario(propietario); // Vinculación única

        if (request.getUsuarioReceptorId() != null) {
            Usuario usuarioReceptor = usuarioRepository.findById(request.getUsuarioReceptorId())
                    .orElseThrow(() -> new RuntimeException("Usuario receptor no encontrado"));
            prestamo.setUsuarioReceptor(usuarioReceptor);
        } else {
            prestamo.setNombreReceptor(request.getNombreReceptor());
        }

        articulo.setEstado("PRESTADO");
        articuloRepository.save(articulo);
        return prestamoRepository.save(prestamo);
    }

    public Prestamo devolverObjeto(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        
        prestamo.setFechaDevuelto(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.FINALIZADO);
        
        Articulo articulo = prestamo.getArticulo();
        articulo.setEstado("DISPONIBLE");
        articuloRepository.save(articulo);
        
        return prestamoRepository.save(prestamo);
    }

    public List<Prestamo> obtenerPrestamosUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioPropietarioId(usuarioId);
    }

    public List<Prestamo> listarPrestamosRecibidos() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    Usuario usuario = usuarioRepository.findByEmail(email);
    
    // Asumiendo que tu PrestamoRepository tiene este método:
    return prestamoRepository.findByUsuarioReceptorId(usuario.getId());
}
// Método para listar lo que A MÍ me han prestado (yo soy el receptor)
public List<Prestamo> listarMisPrestamosRecibidos(Long usuarioId) {
    return prestamoRepository.findByUsuarioReceptorId(usuarioId);
}
}