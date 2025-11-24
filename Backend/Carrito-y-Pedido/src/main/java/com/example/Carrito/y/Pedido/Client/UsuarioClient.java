package com.example.Carrito.y.Pedido.Client;

import com.example.Carrito.y.Pedido.DTO.UsuarioDTO;
import java.util.Optional;

public interface UsuarioClient {
    
    /**
     * Obtiene la información de un usuario desde el microservicio de usuarios.
     * @param id El ID del cliente.
     * @return Un Optional con los datos del usuario si existe.
     */
    Optional<UsuarioDTO> obtenerUsuarioPorId(Long id);
}