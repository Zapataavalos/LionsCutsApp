package com.example.Carrito.y.Pedido.Client;

import org.springframework.web.reactive.function.client.WebClient;
import com.example.Carrito.y.Pedido.DTO.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioClientImpl implements UsuarioClient {

    private final WebClient webClient;

    // Inyectamos la URL base desde el archivo de propiedades
    @Value("${microservicio.usuarios.url}")
    private String usuariosBaseUrl;

    @Autowired
    public UsuarioClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Optional<UsuarioDTO> obtenerUsuarioPorId(Long id) {
        try {
            // Construimos la llamada GET: http://localhost:8081/api/usuarios/{id}
            UsuarioDTO usuario = webClient.get()
                    .uri(usuariosBaseUrl + "/api/usuarios/" + id)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class) // Convertimos la respuesta JSON a nuestro DTO
                    .block(); // .block() hace que la llamada sea síncrona (esperamos la respuesta)
            
            return Optional.ofNullable(usuario);
        } catch (Exception e) {
            // Si hay error (ej: 404 Not Found o el servicio está caído), devolvemos vacío
            // En un entorno real, podrías loguear el error aquí.
            System.out.println("Error al obtener usuario: " + e.getMessage());
            return Optional.empty();
        }
    }
}