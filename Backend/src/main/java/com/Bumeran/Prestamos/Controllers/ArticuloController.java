package com.Bumeran.Prestamos.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Servicios.ArticuloService;
import com.Bumeran.Prestamos.dto.CrearArticuloRequest;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    private final ArticuloService articuloService;

    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    @PostMapping
    public ResponseEntity<?> crearArticulo(@RequestBody CrearArticuloRequest request) {
        try {
            Articulo nuevoArticulo = articuloService.registrarArticulo(request);
            return ResponseEntity.ok(nuevoArticulo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al crear artículo: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Articulo>> obtenerMisArticulos(@PathVariable Long usuarioId) {
        List<Articulo> articulos = articuloService.obtenerMisArticulos(usuarioId);
        return ResponseEntity.ok(articulos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarArticulo(@PathVariable Long id) {
        try {
            articuloService.eliminarArticulo(id);
            return ResponseEntity.ok("Artículo eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}