package com.example.Carrito.y.Pedido.Controller;

import com.example.Carrito.y.Pedido.Model.Carrito;
import com.example.Carrito.y.Pedido.Model.EstadoCarrito;
import com.example.Carrito.y.Pedido.Model.EstadoPedido;
import com.example.Carrito.y.Pedido.Model.ItemCarrito;
import com.example.Carrito.y.Pedido.Model.Pedido;
import com.example.Carrito.y.Pedido.Service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Carga solo el controlador CarritoController para pruebas de capa web
@WebMvcTest(CarritoController.class)
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos Java a JSON

    @MockBean
    private CarritoService carritoService; // Mockear el servicio para aislar la prueba del controlador

    private final Long CLIENTE_ID = 101L;
    private final Long PRODUCTO_ID = 1L;
    private final Long CARRITO_ID = 50L;

    private Carrito carritoActivo;
    private ItemCarrito itemCarrito;

    @BeforeEach
    void setUp() {
        carritoActivo = Carrito.builder()
                .id(CARRITO_ID)
                .clienteId(CLIENTE_ID)
                .estado(EstadoCarrito.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
        
        itemCarrito = ItemCarrito.builder()
                .id(1L)
                .carrito(carritoActivo)
                .productoId(PRODUCTO_ID)
                .cantidad(2)
                .precioUnitario(100.0)
                .duracionUnitarioMinutos(null)
                .build();
    }

    // -----------------------------------------------------------------
    //                  TESTS: GET /carritos/{clienteId}
    // -----------------------------------------------------------------
    
    @Test
    void testObtenerCarritoActivo() throws Exception {
        // Simular que el servicio devuelve un carrito activo
        when(carritoService.obtenerOCrearCarritoActivo(CLIENTE_ID))
                .thenReturn(carritoActivo);

        mockMvc.perform(get("/api/carritos/{clienteId}", CLIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.estado").value(EstadoCarrito.ACTIVO.toString()));
    }

    // -----------------------------------------------------------------
    //                  TESTS: POST /carritos/agregar/{clienteId}
    // -----------------------------------------------------------------

    @Test
    void testAgregarItemAlCarrito() throws Exception {
        // Objeto DTO para la petición POST
        ItemRequest itemRequest = new ItemRequest(PRODUCTO_ID, 2, 100.0, null);

        // Simular que el servicio procesa y devuelve el ítem
        when(carritoService.agregarOActualizarItem(anyLong(), anyLong(), anyInt(), anyDouble(), any()))
                .thenReturn(itemCarrito);

        mockMvc.perform(post("/api/carritos/agregar/{clienteId}", CLIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(PRODUCTO_ID))
                .andExpect(jsonPath("$.cantidad").value(2));
    }
    
    @Test
    void testAgregarItemAlCarrito_ValidacionFalla() throws Exception {
        // Objeto DTO con cantidad inválida (0 o menor)
        ItemRequest itemRequestInvalido = new ItemRequest(PRODUCTO_ID, 0, 100.0, null); 

        // El controlador debe responder con 400 Bad Request debido a la validación @Min(1)
        mockMvc.perform(post("/api/carritos/agregar/{clienteId}", CLIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestInvalido)))
                .andExpect(status().isBadRequest());
    }


    // -----------------------------------------------------------------
    //                  TESTS: DELETE /carritos/eliminar/{clienteId}/{productoId}
    // -----------------------------------------------------------------

    @Test
    void testEliminarItemDelCarrito() throws Exception {
        // Simular que el servicio no lanza excepción al eliminar
        doNothing().when(carritoService).eliminarItem(CLIENTE_ID, PRODUCTO_ID);

        mockMvc.perform(delete("/api/carritos/eliminar/{clienteId}/{productoId}", CLIENTE_ID, PRODUCTO_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Esperamos 204 No Content
    }

    // -----------------------------------------------------------------
    //                  TESTS: POST /carritos/finalizar/{clienteId}
    // -----------------------------------------------------------------

    @Test
    void testFinalizarCompra() throws Exception {
        Pedido pedidoCreado = Pedido.builder()
                .id(100L)
                .clienteId(CLIENTE_ID)
                .total(200.0)
                .estado(EstadoPedido.PENDIENTE_PAGO)
                .build();
        
        // Simular que el servicio convierte el carrito en un pedido
        when(carritoService.finalizarCompra(CLIENTE_ID)).thenReturn(pedidoCreado);

        mockMvc.perform(post("/api/carritos/finalizar/{clienteId}", CLIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(200.0))
                .andExpect(jsonPath("$.estado").value(EstadoPedido.PENDIENTE_PAGO.toString()));
    }
}