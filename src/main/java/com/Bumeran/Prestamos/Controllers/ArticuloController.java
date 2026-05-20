package com.Bumeran.Prestamos.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Servicios.ArticuloService;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    private final ArticuloService articuloService;

    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    @PostMapping
    public ResponseEntity<Articulo> crearArticulo(@RequestBody Articulo articulo) {
        Articulo nuevoArticulo = articuloService.registrarArticulo(articulo);
        return ResponseEntity.ok(nuevoArticulo);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Articulo>> obtenerMisArticulos(@PathVariable Long usuarioId) {
        List<Articulo> articulos = articuloService.obtenerMisArticulos(usuarioId);
        return ResponseEntity.ok(articulos);
    }
}
