package com.Bumeran.Prestamos.Controllers;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Servicios.UsuarioService;
import com.Bumeran.Prestamos.dto.LoginRequest;
import com.Bumeran.Prestamos.dto.RegistroRequest;

@RestController
@RequestMapping("/api/auth") // <--- Esta ruta coincide con el permitAll() de tu SecurityConfig
public class AuthController {

    private final UsuarioService usuarioService;

    // Inyectamos el servicio de usuarios por constructor
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registro de usuarios (Público)
     * POST http://localhost:8080/api/auth/register
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
     * POST http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = usuarioService.login(request);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}