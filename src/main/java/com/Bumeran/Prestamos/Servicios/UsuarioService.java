package com.Bumeran.Prestamos.Servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario en la aplicación.
     * Más adelante, aquí podremos añadir lógica para encriptar la contraseña.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Validación básica: verificar que el email no esté ya registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new RuntimeException("¡Error! Ya existe un usuario registrado con este correo electrónico.");
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su ID. 
     * Si no lo encuentra, lanza una excepción controlada.
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
    }

    // Devuelve todos los usuarios del sistema.
   
    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
}
