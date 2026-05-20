package com.Bumeran.Prestamos.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Entidades.Prestamo;
import com.Bumeran.Prestamos.Servicios.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // Endpoint para registrar un préstamo (Da igual si es usuario registrado o no)
    @PostMapping //no hace falta poner la ruta porque es el mismo endpoint para ambos casos, y solo hay uno 
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Prestamo prestamo) {
        Prestamo nuevoPrestamo = prestamoService.prestarObjeto(prestamo);
        return ResponseEntity.ok(nuevoPrestamo);
    }
    //no hace falta identificar el artículo ni el usuario receptor porque ya vienen en el cuerpo de la petición, y el servicio se encarga de validar que existan y estén disponibles    

    // Añade esto dentro de tu PrestamoController existente:

    // Marcar un préstamo como devuelto
    // PUT http://localhost:8080/api/prestamos/5/devolver (donde 5 es el ID del préstamo)
    @PutMapping("/{id}/devolver")
    public ResponseEntity<Prestamo> devolverArticulo(@PathVariable Long id) {
        Prestamo prestamoDevuelto = prestamoService.devolverObjeto(id);
        return ResponseEntity.ok(prestamoDevuelto);
    }
}