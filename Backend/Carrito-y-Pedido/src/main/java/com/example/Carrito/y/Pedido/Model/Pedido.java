package com.example.Carrito.y.Pedido.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del cliente que realizó la compra
    @Column(nullable = false)
    private Long clienteId; 
    
    // Total de la orden al momento de la compra
    @Column(nullable = false)
    @NotNull
    private Double total; 

    // Referencia al carrito que originó este pedido
    // Útil para la trazabilidad y si el cliente quiere ver su historial de carritos
    @Column(nullable = true)
    private Long carritoOrigenId; 

    // Estado actual del pedido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE_PAGO;

    // Fecha y hora de creación de la orden
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private LocalDateTime ultimaActualizacion;

    // Los ítems del pedido. Usamos ItemPedido (que crearemos a continuación)
    // Se utiliza ItemPedido para guardar una "foto" del ítem al momento de la compra.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items; 

    @PreUpdate
    protected void onUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}