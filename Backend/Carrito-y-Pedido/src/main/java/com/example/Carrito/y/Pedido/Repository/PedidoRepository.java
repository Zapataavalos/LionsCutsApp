package com.example.Carrito.y.Pedido.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Carrito.y.Pedido.Model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Aquí se añadirán métodos de búsqueda más avanzados, como:
    // List<Pedido> findByClienteId(Long clienteId);
    // List<Pedido> findByEstado(EstadoPedido estado);
}
