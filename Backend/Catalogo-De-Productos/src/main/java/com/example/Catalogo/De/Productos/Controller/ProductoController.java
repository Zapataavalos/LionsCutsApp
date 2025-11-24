package com.example.Catalogo.De.Productos.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.Catalogo.De.Productos.Model.Producto;
import com.example.Catalogo.De.Productos.Model.TipoProducto;
import com.example.Catalogo.De.Productos.Service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Catálogo de Productos", description = "Gestión de servicios, productos y recursos en el catálogo.")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // --- 1. CREACIÓN (POST) ---
    @Operation(summary = "Crea un nuevo producto o servicio.")
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardar(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // --- 2. LECTURA (GET) ---

    @Operation(summary = "Obtiene todos los productos y servicios (incluyendo inactivos).")
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @Operation(summary = "Obtiene un producto o servicio por su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto con ID " + id + " no encontrado"));
    }
    
    // --- 3. LECTURA ESPECIALIZADA (Catálogo) ---

    @Operation(summary = "Obtiene solo los productos o servicios marcados como activos (disponibles).")
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> listarActivos() {
        return ResponseEntity.ok(productoService.listarActivos());
    }

    @Operation(summary = "Obtiene productos filtrados por Tipo (SERVICIO, RECURSO, FISICO).")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Producto>> listarPorTipo(@PathVariable TipoProducto tipo) {
        return ResponseEntity.ok(productoService.listarPorTipo(tipo));
    }


    // --- 4. ACTUALIZACIÓN (PUT) ---
    @Operation(summary = "Actualiza completamente un producto existente por ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, 
                                                     @Valid @RequestBody Producto productoDetalles) {
        Producto productoActualizado = productoService.actualizar(id, productoDetalles);
        return ResponseEntity.ok(productoActualizado);
    }


    // --- 5. ELIMINACIÓN (DELETE) ---
    @Operation(summary = "Elimina un producto del catálogo por ID. (Usa con cautela)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
    
    // --- 6. INTEGRACIÓN (Microservicios Check) ---
    @Operation(summary = "Verifica si un producto/servicio existe y está activo.", description = "Usado por otros microservicios (Ej: Citas) para validar reservas.")
    @GetMapping("/check-activo/{id}")
    public ResponseEntity<Boolean> checkProductoActivo(@PathVariable Long id) {
        boolean activo = productoService.existeProductoActivo(id);
        return ResponseEntity.ok(activo);
    }
}