package com.Bumeran.Prestamos.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Servicios.UsuarioService;
import com.Bumeran.Prestamos.dto.LoginRequest;
import com.Bumeran.Prestamos.dto.RegistroRequest;

@RestController
@RequestMapping("/api/auth") 
// Cambiamos a allowCredentials para asegurar que los headers pasen bien
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})

public class AuthController {

    private final UsuarioService usuarioService;

    // Inyectamos el servicio por constructor
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registro de usuarios (Público)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            String mensajeExito = usuarioService.registrarUsuario(request);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", mensajeExito);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Inicio de sesión / Login (Público)
     */
   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        // Aquí es donde el compilador se quejaba antes
        Map<String, Object> resultado = usuarioService.login(request);
        return ResponseEntity.ok(resultado);
    } catch (Exception e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }
}
}