package com.example.Carrito.y.Pedido.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "items_carrito")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al Carrito al que pertenece este ítem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    // ID del producto del Microservicio de Catálogo (clave externa)
    @Column(nullable = false)
    private Long productoId; 

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    // Campos para guardar el precio y la duración en el momento de añadirlo,
    // para evitar inconsistencias si el precio cambia en el catálogo después.
    @Column(nullable = false)
    private Double precioUnitario; 
    
    @Column(nullable = true)
    private Integer duracionUnitarioMinutos; 
}