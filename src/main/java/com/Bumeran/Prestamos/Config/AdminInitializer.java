package com.Bumeran.Prestamos.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {
            String adminEmail = "admin@gmail.com";

            // Buscamos si el administrador ya existe usando el método de tu repositorio actual
            if (usuarioRepository.findByEmail(adminEmail) == null) {

                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setEmail(adminEmail);

                // Encriptamos la contraseña con BCrypt de forma segura
                admin.setPassword(passwordEncoder.encode("admin123"));

                usuarioRepository.save(admin);

                System.out.println("★ ADMINISTRADOR INICIAL DE BUMERAN CREADO ★");
            } else {
                System.out.println("✓ El administrador ya existe en la base de datos.");
            }
        };
    }
}