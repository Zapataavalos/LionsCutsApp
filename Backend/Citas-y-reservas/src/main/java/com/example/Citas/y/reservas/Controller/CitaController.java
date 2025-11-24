package com.example.Citas.y.reservas.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.example.Citas.y.reservas.Model.Cita;
import com.example.Citas.y.reservas.Service.CitaService;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid; // Importante para activar las validaciones

@RestController
@RequestMapping("/api/citas") 
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    // --- 1. ENDPOINTS CRUD ESTÁNDARES ---

    @Operation(summary = "Listar todas las citas")
    @GetMapping
    public ResponseEntity<List<Cita>> listarTodos() {
        return ResponseEntity.ok(citaService.findAll());
    }

    @Operation(summary = "Obtener cita por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return citaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Eliminar cita por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- 2. CREACIÓN CON VALIDACIÓN DE MODELO Y DE NEGOCIO ---

    @Operation(summary = "Crear nueva cita (Reserva) con validación de hora")
    @PostMapping
    // 💡 Aquí usamos @Valid para activar las anotaciones del modelo (@NotNull, @Future, etc.)
    public ResponseEntity<?> crear(@Valid @RequestBody Cita cita) { 
        try {
            Cita nuevaCita = citaService.save(cita);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
        } catch (RuntimeException e) {
            // Manejamos la excepción de conflicto de hora que viene del Servicio
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar una cita existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Cita citaDetails) {
        try {
            Cita citaActualizada = citaService.update(id, citaDetails);
            return ResponseEntity.ok(citaActualizada);
        } catch (RuntimeException e) {
            // Manejamos errores de no encontrado o de conflicto de hora
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    // --- 3. MANEJADOR DE ERRORES DE VALIDACIÓN ---

    /**
     * Este método intercepta automáticamente las excepciones lanzadas 
     * cuando @Valid falla (ej: si fechaHora es nula o pasada).
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Recorremos todos los errores que Spring encontró
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Esto devolverá un JSON limpio con todos los errores al cliente.
        return errors;
    }
}