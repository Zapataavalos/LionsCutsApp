package com.example.Carrito.y.Pedido.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "items_pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al Pedido al que pertenece esta línea
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // ID del producto del Microservicio de Catálogo (referencia)
    @Column(nullable = false)
    private Long productoId; 

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    // Precio que el cliente realmente pagó por unidad.
    @Column(nullable = false)
    private Double precioPagado; 
    
    // Duración registrada al momento de la compra.
    @Column(nullable = true)
    private Integer duracionRegistradaMinutos; 
}
