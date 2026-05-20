package com.Bumeran.Prestamos.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bumeran.Prestamos.Entidades.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring crea automáticamente la query si la llamas así:
    Usuario findByEmail(String email);
}
