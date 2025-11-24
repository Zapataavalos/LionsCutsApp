package com.example.Carrito.y.Pedido.Service;

import com.example.Carrito.y.Pedido.Client.ProductoClient;
import com.example.Carrito.y.Pedido.Client.UsuarioClient;
import com.example.Carrito.y.Pedido.DTO.ProductoDTO;
import com.example.Carrito.y.Pedido.DTO.UsuarioDTO;
import com.example.Carrito.y.Pedido.Model.*;
import com.example.Carrito.y.Pedido.Repository.CarritoRepository;
import com.example.Carrito.y.Pedido.Repository.ItemCarritoRepository;
import com.example.Carrito.y.Pedido.Repository.PedidoRepository;
import com.example.Carrito.y.Pedido.Service.CarritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    // --- NUEVOS MOCKS PARA LOS CLIENTES EXTERNOS ---
    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private ProductoClient productoClient;
    // -----------------------------------------------

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private final Long CLIENTE_ID = 101L;
    private final Long PRODUCTO_ID_SERVICIO = 1L;
    private final Long PRODUCTO_ID_FISICO = 2L;

    private Carrito carritoActivo;
    private ItemCarrito itemServicio;
    
    // DTOs para simular respuestas externas
    private UsuarioDTO usuarioMock;
    private ProductoDTO productoFisicoMock;
    private ProductoDTO productoServicioMock;

    @BeforeEach
    void setUp() {
        // 1. Preparar Datos del Carrito
        carritoActivo = Carrito.builder()
                .id(10L)
                .clienteId(CLIENTE_ID)
                .estado(EstadoCarrito.ACTIVO)
                .items(new ArrayList<>())
                .build();
        
        itemServicio = ItemCarrito.builder()
                .id(100L)
                .carrito(carritoActivo)
                .productoId(PRODUCTO_ID_SERVICIO)
                .cantidad(1)
                .precioUnitario(50.0)
                .duracionUnitarioMinutos(30)
                .build();
        
        carritoActivo.getItems().add(itemServicio);

        // 2. Preparar Datos Simulados de Microservicios Externos
        usuarioMock = UsuarioDTO.builder()
                .id(CLIENTE_ID)
                .nombre("Cliente Test")
                .email("test@example.com")
                .build();

        productoFisicoMock = ProductoDTO.builder()
                .id(PRODUCTO_ID_FISICO)
                .nombre("Producto Fisico")
                .precio(25.0) // Precio Real del Catálogo
                .duracionMinutos(0)
                .tipo("FISICO")
                .activo(true)
                .build();

        productoServicioMock = ProductoDTO.builder()
                .id(PRODUCTO_ID_SERVICIO)
                .nombre("Servicio")
                .precio(50.0)
                .duracionMinutos(30)
                .tipo("SERVICIO")
                .activo(true)
                .build();
    }

    // -----------------------------------------------------------------
    //                  TESTS: OBTENER/CREAR CARRITO
    // -----------------------------------------------------------------

    @Test
    void testObtenerCarritoActivoExistente() {
        when(carritoRepository.findByClienteIdAndEstado(CLIENTE_ID, EstadoCarrito.ACTIVO))
            .thenReturn(Optional.of(carritoActivo));

        Carrito resultado = carritoService.obtenerOCrearCarritoActivo(CLIENTE_ID);

        assertEquals(carritoActivo.getId(), resultado.getId());
        // No debe llamar al usuarioClient si el carrito ya existe (según tu lógica actual)
        // O si tu lógica valida siempre, aquí deberías agregar el mock.
        // Asumiendo que findBy devuelve algo, no entra al orElseGet donde está la validación.
    }

    @Test
    void testCrearNuevoCarritoActivo() {
        // Simulamos que NO existe carrito
        when(carritoRepository.findByClienteIdAndEstado(CLIENTE_ID, EstadoCarrito.ACTIVO))
            .thenReturn(Optional.empty());
        
        // NUEVO: Simulamos que el Usuario SÍ existe en el microservicio externo
        when(usuarioClient.obtenerUsuarioPorId(CLIENTE_ID)).thenReturn(Optional.of(usuarioMock));

        when(carritoRepository.save(any(Carrito.class)))
            .thenAnswer(invocation -> {
                Carrito nuevo = invocation.getArgument(0);
                nuevo.setId(11L);
                return nuevo;
            });

        Carrito resultado = carritoService.obtenerOCrearCarritoActivo(CLIENTE_ID);

        assertNotNull(resultado.getId());
        assertEquals(EstadoCarrito.ACTIVO, resultado.getEstado());
        verify(usuarioClient).obtenerUsuarioPorId(CLIENTE_ID); // Verificamos que se llamó al cliente
    }

    // -----------------------------------------------------------------
    //                  TESTS: AGREGAR/ACTUALIZAR ÍTEM
    // -----------------------------------------------------------------

    @Test
    void testAgregarNuevoItem() {
        int cantidad = 2;
        // Precio simulado que envía el usuario (debe ser ignorado por el servicio)
        Double precioEnviado = 9999.0; 

        // 1. Simular Carrito
        when(carritoRepository.findByClienteIdAndEstado(CLIENTE_ID, EstadoCarrito.ACTIVO))
            .thenReturn(Optional.of(carritoActivo));
        
        // 2. NUEVO: Simular respuesta del Catálogo de Productos
        when(productoClient.obtenerProductoPorId(PRODUCTO_ID_FISICO))
            .thenReturn(Optional.of(productoFisicoMock));

        // 3. Simular que el ítem no existe en el carrito
        when(itemCarritoRepository.findByCarritoIdAndProductoId(carritoActivo.getId(), PRODUCTO_ID_FISICO))
            .thenReturn(Optional.empty());
            
        when(itemCarritoRepository.save(any(ItemCarrito.class)))
            .thenAnswer(invocation -> {
                ItemCarrito nuevoItem = invocation.getArgument(0);
                nuevoItem.setId(101L);
                return nuevoItem;
            });
        
        // Ejecución
        ItemCarrito resultado = carritoService.agregarOActualizarItem(
            CLIENTE_ID, PRODUCTO_ID_FISICO, cantidad, precioEnviado, 0);

        assertNotNull(resultado.getId());
        // VERIFICACIÓN CLAVE: El precio debe ser el del Mock (25.0), NO el enviado (9999.0)
        assertEquals(productoFisicoMock.getPrecio(), resultado.getPrecioUnitario());
        verify(productoClient).obtenerProductoPorId(PRODUCTO_ID_FISICO);
    }

    @Test
    void testActualizarItemExistente() {
        int nuevaCantidad = 5;

        // NUEVO: Simular respuesta del Catálogo
        when(productoClient.obtenerProductoPorId(PRODUCTO_ID_SERVICIO))
            .thenReturn(Optional.of(productoServicioMock));

        when(carritoRepository.findByClienteIdAndEstado(CLIENTE_ID, EstadoCarrito.ACTIVO))
            .thenReturn(Optional.of(carritoActivo));
        when(itemCarritoRepository.findByCarritoIdAndProductoId(carritoActivo.getId(), PRODUCTO_ID_SERVICIO))
            .thenReturn(Optional.of(itemServicio));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenReturn(itemServicio);

        ItemCarrito resultado = carritoService.agregarOActualizarItem(
            CLIENTE_ID, PRODUCTO_ID_SERVICIO, nuevaCantidad, 50.0, 30);

        assertEquals(nuevaCantidad, resultado.getCantidad());
        verify(productoClient).obtenerProductoPorId(PRODUCTO_ID_SERVICIO);
    }

    @Test
    void testAgregarItemProductoNoExisteLanzaExcepcion() {
        // Simulamos que el producto NO existe en el catálogo
        when(productoClient.obtenerProductoPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> 
            carritoService.agregarOActualizarItem(CLIENTE_ID, 99L, 1, 10.0, 0));
    }

    // -----------------------------------------------------------------
    //                  TESTS: ELIMINAR ÍTEM
    // -----------------------------------------------------------------
    
    @Test
    void testEliminarItemExitoso() {
        when(carritoRepository.findByClienteIdAndEstado(CLIENTE_ID, EstadoCarrito.ACTIVO))
            .thenReturn(Optional.of(carritoActivo));
        when(itemCarritoRepository.findByCarritoIdAndProductoId(carritoActivo.getId(), PRODUCTO_ID_SERVICIO))
            .thenReturn(Optional.of(itemServicio));

        carritoService.eliminarItem(CLIENTE_ID, PRODUCTO_ID_SERVICIO);

        verify(itemCarritoRepository, times(1)).delete(itemServicio);
        verify(carritoRepository, times(1)).save(carritoActivo);
    }

    // -----------------------------------------------------------------
    //                  TESTS: CÁLCULOS
    // -----------------------------------------------------------------

    @Test
    void testCalcularTotal() {
        when(carritoRepository.findById(carritoActivo.getId())).thenReturn(Optional.of(carritoActivo));
        Double total = carritoService.calcularTotal(carritoActivo.getId());
        assertEquals(50.0, total);
    }
    
    // -----------------------------------------------------------------
    //              TESTS: FINALIZAR COMPRA
    // -----------------------------------------------------------------

    @Test
    void testFinalizarCompraExitosa() {
        // NUEVO: Simular que el Usuario existe (Validación al inicio de finalizarCompra)
        when(usuarioClient.obtenerUsuarioPorId(CLIENTE_ID)).thenReturn(Optional.of(usuarioMock));

        // Mocks de repositorios
        when(carritoRepository.findById(carritoActivo.getId())).thenReturn(Optional.of(carritoActivo)); 
        when(carritoRepository.findByClienteIdAndEstado(CLIENTE_ID, EstadoCarrito.ACTIVO))
            .thenReturn(Optional.of(carritoActivo));
        
        when(pedidoRepository.save(any(Pedido.class)))
            .thenAnswer(invocation -> {
                Pedido pedido = invocation.getArgument(0);
                pedido.setId(200L); 
                return pedido;
            });
        
        when(carritoRepository.save(any(Carrito.class)))
            .thenAnswer(invocation -> invocation.getArgument(0)); 

        Pedido resultado = carritoService.finalizarCompra(CLIENTE_ID);

        assertNotNull(resultado.getId());
        assertEquals(50.0, resultado.getTotal()); 
        assertEquals(EstadoCarrito.COMPLETADO, carritoActivo.getEstado()); 
        
        verify(usuarioClient).obtenerUsuarioPorId(CLIENTE_ID); // Verifica llamada al microservicio usuario
    }
}