package com.example.Citas.y.reservas.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Importante para las validaciones
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Conexiones (Foreign Keys Lógicas) ---

    // El ID del usuario que realiza la reserva (debe existir)
    @Column(nullable = false)
    @NotNull(message = "El ID del usuario no puede ser nulo")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long usuarioId; 

    // El ID del servicio/producto reservado (debe existir)
    @Column(nullable = false)
    @NotNull(message = "El ID del servicio no puede ser nulo")
    @Positive(message = "El ID del servicio debe ser un número positivo")
    private Long servicioId; 

    // --- Detalles de la Reserva ---

    // La fecha y hora de la cita.
    @Column(nullable = false)
    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @Future(message = "La reserva debe ser para una fecha futura") // Solo permite fechas posteriores al momento actual
    private LocalDateTime fechaHora;
    
    // Duración de la cita en minutos (ej: 30, 60, 90)
    @Column(nullable = false)
    @NotNull(message = "La duración de la cita es obligatoria")
    @Min(value = 15, message = "La duración mínima es de 15 minutos")
    private Integer duracionMinutos;

    // --- Estado y Notas ---
    
    // Estado de la reserva: PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado de la cita no puede ser nulo")
    private EstadoCita estado = EstadoCita.PENDIENTE; // Valor por defecto

    // Notas o requerimientos especiales del cliente
    @Size(max = 500, message = "Las notas no pueden exceder los 500 caracteres")
    private String notas;
    
    // Fecha de creación de la reserva (Generado automáticamente al guardar)
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    // Fecha de última modificación
    private LocalDateTime fechaActualizacion;

    // Método que se ejecuta antes de guardar o actualizar (útil para actualizar la fecha)
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}