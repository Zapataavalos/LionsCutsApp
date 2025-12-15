package com.example.Usuario.Controller;

import com.example.Usuario.Model.Usuario;
import com.example.Usuario.Repository.UsuarioRepository;
import com.example.Usuario.Service.UsuarioService;
import com.example.Usuario.Dto.PasswordChangeRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ======================================================
    // AUTENTICACIÓN
    // ======================================================

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioGuardado = usuarioService.register(usuario);
            usuarioGuardado.setPassword(null);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        Usuario user = usuarioRepository.findByEmail(usuario.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        if (!passwordEncoder.matches(usuario.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Contraseña incorrecta");
        }

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // ======================================================
    // USUARIOS
    // ======================================================

    @GetMapping("/api/usuarios/all")
    public ResponseEntity<?> getAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/api/usuarios/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        Usuario user = usuarioRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/usuarios/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {

        Usuario user = usuarioRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // ======================================================
    // ACTUALIZAR PERFIL (SIN PASSWORD)
    // ======================================================

    @PutMapping("/api/usuarios/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario usuario) {

        Usuario user = usuarioRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        user.setNombre(usuario.getNombre());
        user.setApellido(usuario.getApellido());
        user.setEmail(usuario.getEmail());
        user.setTelefono(usuario.getTelefono());
        user.setRol(usuario.getRol());

        usuarioRepository.save(user);

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // ======================================================
    // CAMBIO DE CONTRASEÑA
    // ======================================================

    @PutMapping("/api/usuarios/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody PasswordChangeRequest request
    ) {

        Usuario user = usuarioRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        usuarioRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // ======================================================
    // ELIMINAR
    // ======================================================

    @DeleteMapping("/api/usuarios/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Usuario user = usuarioRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        usuarioRepository.delete(user);
        return ResponseEntity.ok("Usuario eliminado");
    }
}
