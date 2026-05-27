package com.Bumeran.Prestamos.Controllers;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Servicios.UsuarioService;
import com.Bumeran.Prestamos.dto.UsuarioUpdateRequest;

@RestController
@RequestMapping("/api/usuarios") // <--- Cambiamos la ruta para que requiera Token JWT
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Listar todos los usuarios (Ruta protegida)
     * GET http://localhost:8080/api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodosLosUsuarios());
    }

    /**
     * Obtener un usuario por ID (Ruta protegida)
     * GET http://localhost:8080/api/usuarios/{id}
     */
    @GetMapping("buscar/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

  @PutMapping("/editar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
        try {
            // 1. Buscamos el usuario por su ID usando tu servicio existente
            Usuario usuarioExistente = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioExistente == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            // 2. Ejecutamos la lógica de edición delegando en el servicio (como en tu otro proyecto)
            Usuario usuarioActualizado = usuarioService.editarUsuario(usuarioExistente, request);

            // 3. Devolvemos el usuario modificado con un 200 OK
            return ResponseEntity.ok(usuarioActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }
    }
