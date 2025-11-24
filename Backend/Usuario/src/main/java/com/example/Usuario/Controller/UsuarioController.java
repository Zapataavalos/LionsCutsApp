package com.example.Usuario.Controller;

import com.example.Usuario.Model.Usuario;
import com.example.Usuario.Repository.UsuarioRepository;
import com.example.Usuario.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// @RequestMapping("/api/auth") <--- LO QUITAMOS para poder usar diferentes rutas base
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ======================================================
    // SECCIÓN: AUTENTICACIÓN (/api/auth)
    // ======================================================

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/api/auth/register") // <--- Ruta explícita
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioGuardado = usuarioService.register(usuario);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Login simple")
    @PostMapping("/api/auth/login") // <--- Ruta explícita
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        Usuario user = usuarioRepository.findByUsername(usuario.getUsername()).orElse(null);

        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        if (!passwordEncoder.matches(usuario.getPassword(), user.getPassword()))
            return ResponseEntity.badRequest().body("Contraseña incorrecta");

        return ResponseEntity.ok(user);
    }


    // ======================================================
    // SECCIÓN: USUARIOS (/api/usuarios)
    // Esta es la sección que usará el Microservicio de Carrito
    // ======================================================

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping("/api/usuarios/all")
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // --- ESTE ES EL MÉTODO QUE LLAMA EL CARRITO ---
    // Antes era: /api/auth/find/{id}
    // Ahora es:  /api/usuarios/{id}  <--- Coincide con UsuarioClientImpl
    @Operation(summary = "Buscar usuario por ID")
    @GetMapping("/api/usuarios/{id}") 
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElseGet(() -> ResponseEntity.badRequest().body("Usuario no encontrado"));
    }
    //Buscar usuario por email
    @Operation(summary = "Buscar usuario por email")
    @PostMapping("/api/usuarios/email/{email}") // <--- Ruta explícita
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        Usuario user = usuarioRepository.findByEmail(email).orElse(null);

        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/api/usuarios/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNombre(usuario.getNombre());
                    u.setApellido(usuario.getApellido());
                    u.setEmail(usuario.getEmail());
                    u.setTelefono(usuario.getTelefono());
                    u.setRol(usuario.getRol());
                    
                    if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                        u.setPassword(passwordEncoder.encode(usuario.getPassword()));
                    }
                    
                    usuarioRepository.save(u);
                    return ResponseEntity.ok((Object) u);
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Usuario no encontrado"));
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/api/usuarios/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(u -> {
            usuarioRepository.delete(u);
            return ResponseEntity.ok("Usuario eliminado");
        }).orElseGet(() -> ResponseEntity.badRequest().body("Usuario no encontrado"));
    }

}