package com.example.Usuario.Service;

import com.example.Usuario.Model.Usuario;
import com.example.Usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario register(Usuario usuario) {

        // 1️⃣ Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        // 2️⃣ 🔥 CLAVE: usar email como username
        usuario.setUsername(usuario.getEmail());

        // 3️⃣ Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 4️⃣ Rol por defecto
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("cliente");
        }

        // 5️⃣ Guardar usuario
        return usuarioRepository.save(usuario);
    }
}
