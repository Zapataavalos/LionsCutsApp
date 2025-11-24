package com.example.Carrito.y.Pedido.Client;

import com.example.Carrito.y.Pedido.DTO.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class ProductoClientImpl implements ProductoClient {

    private final WebClient webClient;

    // Inyectamos la URL base (se configurará en application.properties)
    @Value("${microservicio.productos.url}")
    private String productosBaseUrl;

    @Autowired
    public ProductoClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Optional<ProductoDTO> obtenerProductoPorId(Long id) {
        try {
            // Llamada GET: http://localhost:8083/api/productos/{id}
            ProductoDTO producto = webClient.get()
                    .uri(productosBaseUrl + "/api/productos/" + id)
                    .retrieve()
                    .bodyToMono(ProductoDTO.class)
                    .block();
            
            return Optional.ofNullable(producto);
        } catch (Exception e) {
            System.out.println("Error al obtener producto: " + e.getMessage());
            return Optional.empty();
        }
    }
}