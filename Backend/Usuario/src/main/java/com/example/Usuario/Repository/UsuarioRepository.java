package com.example.Usuario.Repository;

import com.example.Usuario.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por username
    Optional<Usuario> findByUsername(String username);

    // Verificar si ya existe un username
    boolean existsByUsername(String username);

    // Verificar si ya existe un email
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
