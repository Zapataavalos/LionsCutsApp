package com.example.Usuario.Controller;

import com.example.Usuario.Config.SecurityConfig;
import com.example.Usuario.Model.Usuario;
import com.example.Usuario.Repository.UsuarioRepository;
import com.example.Usuario.Service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class) // Importante: Traemos tu configuración de seguridad
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Autowired // Inyectamos el real (o mockeado) del SecurityConfig
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @Test
    void register_DeberiaRetornar200_CuandoRegistroEsExitoso() throws Exception {
        Usuario usuario = Usuario.builder()
                .username("nuevo")
                .password("12345")
                .build();

        when(usuarioService.register(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nuevo"));
    }

    @Test
    void login_DeberiaRetornar200_CuandoCredencialesSonCorrectas() throws Exception {
        // Setup: Crear un usuario con contraseña encriptada (simulada)
        String passPlano = "12345";
        String passHash = passwordEncoder.encode(passPlano);
        
        Usuario usuarioDB = Usuario.builder()
                .username("admin")
                .password(passHash) 
                .build();

        Usuario loginRequest = Usuario.builder()
                .username("admin")
                .password(passPlano)
                .build();

        // Mock del repositorio para el login
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioDB));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Login exitoso: admin")); // Verificamos el mensaje de texto
    }
    
    @Test
    void login_DeberiaRetornar400_CuandoUsuarioNoExiste() throws Exception {
        Usuario loginRequest = Usuario.builder().username("fantasma").password("123").build();
        
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}