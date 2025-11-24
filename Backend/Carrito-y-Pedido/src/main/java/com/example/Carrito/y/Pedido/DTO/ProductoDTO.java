package com.example.Carrito.y.Pedido.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio; // El precio REAL
    private Integer duracionMinutos;
    private String tipo; // "FISICO" o "SERVICIO"
    private boolean activo;
}