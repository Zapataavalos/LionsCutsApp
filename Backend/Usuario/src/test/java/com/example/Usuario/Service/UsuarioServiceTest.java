package com.example.Usuario.Service;

import com.example.Usuario.Model.Usuario;
import com.example.Usuario.Repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void register_DeberiaGuardarUsuario_CuandoDatosSonValidos() {
        // 1. GIVEN (Datos de prueba)
        Usuario usuario = Usuario.builder()
                .username("testuser")
                .password("12345")
                .email("test@email.com")
                .build();

        // Simulamos que NO existe ni el username ni el email
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        // Simulamos la encriptación
        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");
        // Simulamos el guardado (devuelve el mismo usuario)
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // 2. WHEN (Ejecutamos el método)
        Usuario resultado = usuarioService.register(usuario);

        // 3. THEN (Verificaciones)
        assertNotNull(resultado);
        assertEquals("encodedPassword", resultado.getPassword()); // Verificamos que se haya encriptado
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); // Verificamos que se llamó a guardar
    }

    @Test
    void register_DeberiaLanzarError_CuandoUsernameYaExiste() {
        // 1. GIVEN
        Usuario usuario = Usuario.builder().username("duplicado").build();
        
        // Simulamos que SI existe
        when(usuarioRepository.existsByUsername("duplicado")).thenReturn(true);

        // 2. WHEN & THEN (Esperamos una excepción)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.register(usuario);
        });

        assertEquals("El username ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any()); // Aseguramos que NUNCA se guardó
    }
}