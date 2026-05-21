package com.Bumeran.Prestamos.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Configuration;

import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = "admin@gmail.com";

            // Comprobamos si el administrador ya existe usando tu repositorio actual
            if (usuarioRepository.findByEmail(adminEmail) == null) {

                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setEmail(adminEmail);

                // IMPORTANTE: Encriptamos la contraseña con BCrypt
                admin.setPassword(passwordEncoder.encode("admin123"));

                usuarioRepository.save(admin);

                System.out.println("★ ADMINISTRADOR INICIAL DE BUMERAN CREADO ★");
            } else {
                System.out.println("✓ El administrador ya existe en la base de datos.");
            }
        };
    }
}
