package com.example.Carrito.y.Pedido.Client;

import com.example.Carrito.y.Pedido.DTO.ProductoDTO;
import java.util.Optional;

public interface ProductoClient {
    Optional<ProductoDTO> obtenerProductoPorId(Long id);
}