package com.example.Carrito.y.Pedido.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Carrito.y.Pedido.Model.ItemCarrito;
import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    /**
     * Busca un ítem específico en un carrito, basado en el ID del producto.
     * Esto es necesario para incrementar la cantidad si el producto ya existe.
     * @param carritoId El ID del carrito.
     * @param productoId El ID del producto del Catálogo.
     * @return El ítem de carrito que coincide con el producto y el carrito.
     */
    Optional<ItemCarrito> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
    
    /**
     * Busca todos los ítems que pertenecen a un carrito específico.
     * @param carritoId El ID del carrito.
     * @return Una lista de ItemCarrito.
     */
    List<ItemCarrito> findByCarritoId(Long carritoId);
}
