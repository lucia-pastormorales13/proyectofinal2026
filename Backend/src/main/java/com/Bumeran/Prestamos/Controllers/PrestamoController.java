package com.Bumeran.Prestamos.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Entidades.Prestamo;
import com.Bumeran.Prestamos.Servicios.PrestamoService;
import com.Bumeran.Prestamos.dto.CrearPrestamoRequest;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPrestamo(@RequestBody CrearPrestamoRequest request) {
        try {
            Prestamo nuevoPrestamo = prestamoService.prestarObjeto(request);
            return ResponseEntity.ok(nuevoPrestamo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear préstamo: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<Prestamo> devolverArticulo(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolverObjeto(id));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Prestamo>> obtenerPrestamosUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPrestamosUsuario(id));
    }

    @GetMapping("/mis-prestamos")
    public ResponseEntity<List<Prestamo>> getMisPrestamos() {
        return ResponseEntity.ok(prestamoService.listarMisPrestamos());
    }

    @GetMapping("/prestamos-recibidos")
public ResponseEntity<List<Prestamo>> getPrestamosRecibidos() {
    // Este método buscará préstamos donde TU seas el receptor
    return ResponseEntity.ok(prestamoService.listarPrestamosRecibidos());
}
}