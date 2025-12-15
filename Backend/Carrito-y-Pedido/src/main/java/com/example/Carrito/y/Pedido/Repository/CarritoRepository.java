package com.example.Carrito.y.Pedido.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Carrito.y.Pedido.Model.Carrito;
import com.example.Carrito.y.Pedido.Model.EstadoCarrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /**
     * Busca un carrito por el ID del cliente y por su estado.
     * (Ej: carrito ACTIVO)
     */
    Optional<Carrito> findByClienteIdAndEstado(Long clienteId, EstadoCarrito estado);

    /**
     * Busca un carrito por su ID y estado.
     * 👉 NECESARIO para validar carritos activos
     */
    Optional<Carrito> findByIdAndEstado(Long id, EstadoCarrito estado);

    /**
     * Busca todos los carritos de un cliente (historial)
     */
    List<Carrito> findByClienteId(Long clienteId);
}
