package com.example.Catalogo.De.Productos.Controller;

import com.example.Catalogo.De.Productos.Model.Producto;
import com.example.Catalogo.De.Productos.Model.TipoProducto;
import com.example.Catalogo.De.Productos.Service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto(1L, "Manicura Clásica", "Servicio de uñas", 
                                25.0, 45, TipoProducto.SERVICIO, true, 
                                LocalDateTime.now(), null);
    }

    // --- TEST: CREAR PRODUCTO (POST) ---
    @Test
    void testCrearProductoExitoso() throws Exception {
        when(productoService.guardar(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated()) // Espera HTTP 201
                .andExpect(jsonPath("$.nombre").value("Manicura Clásica"));

        verify(productoService, times(1)).guardar(any(Producto.class));
    }
    
    @Test
    void testCrearProductoFalloValidacion() throws Exception {
        // Creamos un producto INVÁLIDO (nombre nulo, falla @NotBlank)
        Producto productoInvalido = new Producto();
        productoInvalido.setPrecio(20.0); // Campos obligatorios faltan

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoInvalido)))
                .andExpect(status().isBadRequest()) // Espera HTTP 400 por la validación @Valid
                .andExpect(jsonPath("$.nombre").doesNotExist());
        
        // Verificamos que el servicio NUNCA fue llamado
        verify(productoService, never()).guardar(any(Producto.class));
    }

    // --- TEST: OBTENER POR ID (GET) ---
    @Test
    void testObtenerPorIdExistente() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.nombre").value("Manicura Clásica"));

        verify(productoService, times(1)).obtenerPorId(1L);
    }
    
    @Test
    void testObtenerPorIdNoExistente() throws Exception {
        when(productoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera HTTP 404

        verify(productoService, times(1)).obtenerPorId(99L);
    }

    // --- TEST: LISTAR ACTIVOS (GET) ---
    @Test
    void testListarActivos() throws Exception {
        when(productoService.listarActivos()).thenReturn(Arrays.asList(producto));

        mockMvc.perform(get("/api/productos/activos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$[0].nombre").value("Manicura Clásica"));

        verify(productoService, times(1)).listarActivos();
    }
    
    // --- TEST: ELIMINAR (DELETE) ---
    @Test
    void testEliminarProducto() throws Exception {
        // No necesitamos simular el findById, solo que el servicio ejecuta la eliminación sin error
        doNothing().when(productoService).eliminar(1L); 

        mockMvc.perform(delete("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Espera HTTP 204

        verify(productoService, times(1)).eliminar(1L);
    }
}