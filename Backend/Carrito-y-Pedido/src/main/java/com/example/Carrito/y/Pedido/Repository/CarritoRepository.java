package com.example.Carrito.y.Pedido.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Carrito.y.Pedido.Model.Carrito;
import com.example.Carrito.y.Pedido.Model.EstadoCarrito;


public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /**
     * Busca un carrito por el ID del cliente y por su estado.
     * Esto es crucial para encontrar el carrito ACTIVO de un usuario.
     * @param clienteId El ID del cliente.
     * @param estado El estado del carrito (e.g., ACTIVO).
     * @return El carrito que cumple con los criterios.
     */
    Optional<Carrito> findByClienteIdAndEstado(Long clienteId, EstadoCarrito estado);
    
    /**
     * Busca un carrito por el ID del cliente, sin importar el estado.
     * @param clienteId El ID del cliente.
     * @return Una lista de carritos asociados al cliente.
     */
    List<Carrito> findByClienteId(Long clienteId);
}
