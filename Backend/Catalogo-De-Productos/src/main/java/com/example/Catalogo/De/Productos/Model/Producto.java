package com.example.Catalogo.De.Productos.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Descripción General ---

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre del producto/servicio es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;
    
    // --- Precios y Duración (MODIFICADO) ---

    // Precio del servicio (Ej: 50.00)
    @Column(nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser cero o positivo")
    private Double precio; 

    // Duración en minutos. Se ha hecho opcional para productos físicos.
    @Column(nullable = true) // <--- CAMBIO 1: Permitir NULL en la DB
    // @NotNull ya no es necesario si el campo es opcional
    @Min(value = 0, message = "La duración mínima debe ser de 0 minutos para productos físicos") // <--- CAMBIO 2: Aceptar 0
    private Integer duracionMinutos;

    // --- Categorización y Estado ---
    
    // Tipo de producto (EJ: 'Servicio', 'Producto Físico', 'Recurso/Empleado')
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de producto es obligatorio")
    private TipoProducto tipo; 

    // Estado de activación (true: disponible, false: retirado/inactivo)
    @Column(nullable = false)
    private boolean activo = true;

    // --- Auditoría ---
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}