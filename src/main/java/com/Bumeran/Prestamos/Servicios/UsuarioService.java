package com.Bumeran.Prestamos.Servicios;

import java.util.List;

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

    // Inyección de dependencias por constructor (añadimos encoder y jwt)
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario en la aplicación utilizando el DTO.
     * Ahora SÍ encripta la contraseña de forma segura antes de guardarla.
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

    /**
     * Autentica a un usuario y le devuelve su token JWT si las credenciales son válidas.
     */
    public String login(LoginRequest request) {
        // Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
        
        if (usuario == null) {
            throw new RuntimeException("Credenciales incorrectas (Email no encontrado)");
        }

        // Verificar si la contraseña coincide con el hash guardado
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas (Contraseña inválida)");
        }

        // Si todo coincide, generamos el Token JWT con su email
        return jwtService.generateToken(usuario.getEmail());
    }

    /**
     * Busca un usuario por su ID. 
     * Si no lo encuentra, lanza una excepción controlada.
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

public Usuario editarUsuario(Usuario usuarioExistente, UsuarioUpdateRequest datosNuevos) {
    // 1. Modificar nombre si viene en la petición
    if (datosNuevos.getNombre() != null && !datosNuevos.getNombre().isEmpty()) {
        usuarioExistente.setNombre(datosNuevos.getNombre());
    }
    
    // 2. Modificar email si viene en la petición
    if (datosNuevos.getEmail() != null && !datosNuevos.getEmail().isEmpty()) {
        usuarioExistente.setEmail(datosNuevos.getEmail());
    }
    
    // 3. Modificar y encriptar contraseña si viene una nueva
    if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        usuarioExistente.setPassword(encoder.encode(datosNuevos.getPassword()));
    }
    
    // 4. Guardar en la base de datos
    return usuarioRepository.save(usuarioExistente);
}
}