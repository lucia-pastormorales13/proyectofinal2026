package com.Bumeran.Prestamos.Servicios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;
import com.Bumeran.Prestamos.dto.LoginRequest;
import com.Bumeran.Prestamos.dto.RegistroRequest;
import com.Bumeran.Prestamos.dto.UsuarioUpdateRequest;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Inyección de dependencias por constructor
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario en la aplicación utilizando el DTO.
     */
    public String registrarUsuario(RegistroRequest request) {
        // Validación: verificar que el email no esté ya registrado
        if (usuarioRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("¡Error! Ya existe un usuario registrado con este correo electrónico.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        
        // Encriptamos la contraseña con BCrypt antes de guardarla en la BD
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepository.save(usuario);
        
        return "Usuario registrado correctamente";
    }

   public Map<String, Object> login(LoginRequest request) {
    Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
    
    if (usuario == null) throw new RuntimeException("Credenciales incorrectas");
    if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
        throw new RuntimeException("Credenciales incorrectas");
    }

    String token = jwtService.generateToken(usuario.getEmail());
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("token", token);
    respuesta.put("id", usuario.getId()); // Frontend espera esto
    
    return respuesta;
}
    /**
     * Busca un usuario por su ID. 
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
    }

    /**
     * Devuelve todos los usuarios del sistema.
     */
    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Modifica los datos de un usuario existente.
     */
    public Usuario editarUsuario(Usuario usuarioExistente, UsuarioUpdateRequest datosNuevos) {
        if (datosNuevos.getNombre() != null && !datosNuevos.getNombre().isEmpty()) {
            usuarioExistente.setNombre(datosNuevos.getNombre());
        }
        
        if (datosNuevos.getEmail() != null && !datosNuevos.getEmail().isEmpty()) {
            usuarioExistente.setEmail(datosNuevos.getEmail());
        }
        
        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            usuarioExistente.setPassword(encoder.encode(datosNuevos.getPassword()));
        }
        
        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario autenticarYObtenerUsuario(String email, String password) {
        throw new UnsupportedOperationException("Unimplemented method 'autenticarYObtenerUsuario'");
    }
}