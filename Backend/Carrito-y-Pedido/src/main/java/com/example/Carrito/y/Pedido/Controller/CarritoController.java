package com.example.Carrito.y.Pedido.Controller;

import com.example.Carrito.y.Pedido.Model.*;
import com.example.Carrito.y.Pedido.Service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

// Clase DTO simple para añadir ítems (Data Transfer Object)
record ItemRequest(
    @NotNull Long productoId, 
    @Min(1) int cantidad,
    @NotNull Double precioUnitario,
    Integer duracionUnitarioMinutos // Puede ser null
) {}

@RestController
@RequestMapping("/api/carritos")
@Tag(name = "Carritos y Pedidos", description = "Gestión del carrito de compras y finalización de pedidos.")
public class CarritoController {

    @Autowired
    private CarritoService carritoService; // <-- Instancia inyectada

    // --- 1. GESTIÓN DEL CARRITO ---
    
    @Operation(summary = "Obtiene el carrito activo de un cliente específico. Si no existe, lo crea.")
    @GetMapping("/{clienteId}")
    public ResponseEntity<Carrito> obtenerCarritoActivo(@PathVariable Long clienteId) {
        Carrito carrito = carritoService.obtenerOCrearCarritoActivo(clienteId);
        return ResponseEntity.ok(carrito);
    }

    @Operation(summary = "Agrega o actualiza un ítem (producto/servicio) al carrito del cliente.")
    @PostMapping("/agregar/{clienteId}")
    public ResponseEntity<ItemCarrito> agregarItemAlCarrito(
            @PathVariable Long clienteId,
            @Valid @RequestBody ItemRequest itemRequest) {
        
        // --- CORRECCIÓN APLICADA: Uso de 'carritoService' ---
        ItemCarrito item = carritoService.agregarOActualizarItem( 
            clienteId,
            itemRequest.productoId(),
            itemRequest.cantidad(),
            itemRequest.precioUnitario(),
            itemRequest.duracionUnitarioMinutos()
        );
        // ----------------------------------------------------
        
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Elimina un ítem específico del carrito de un cliente.")
    @DeleteMapping("/eliminar/{clienteId}/{productoId}")
    public ResponseEntity<Void> eliminarItemDelCarrito(
            @PathVariable Long clienteId,
            @PathVariable Long productoId) {
        
        carritoService.eliminarItem(clienteId, productoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- 2. CÁLCULOS ---
    
    @Operation(summary = "Calcula el total monetario del carrito.")
    @GetMapping("/total/{carritoId}")
    public ResponseEntity<Double> calcularTotal(@PathVariable Long carritoId) {
        Double total = carritoService.calcularTotal(carritoId);
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Calcula la duración total de los servicios en el carrito (en minutos).")
    @GetMapping("/duracion/{carritoId}")
    public ResponseEntity<Integer> calcularDuracionTotal(@PathVariable Long carritoId) {
        Integer duracion = carritoService.calcularDuracionTotalMinutos(carritoId);
        return ResponseEntity.ok(duracion);
    }
    
    // --- 3. PROCESO DE PEDIDO ---

    @Operation(summary = "Convierte el carrito activo del cliente en un Pedido final, iniciando el flujo de pago.")
    @PostMapping("/finalizar/{clienteId}")
    public ResponseEntity<Pedido> finalizarCompra(@PathVariable Long clienteId) {
        Pedido pedido = carritoService.finalizarCompra(clienteId);
        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }
}