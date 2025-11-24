package com.example.Catalogo.De.Productos.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.example.Catalogo.De.Productos.Model.Producto;
import com.example.Catalogo.De.Productos.Model.TipoProducto;
import com.example.Catalogo.De.Productos.Repository.ProductoRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoServicio; // Renombrado de productoActivo
    private Producto productoFisico; // Nuevo objeto para probar duración 0
    private Producto productoInactivo;

    @BeforeEach
    void setUp() {
        // Producto de Servicio (Duración > 0)
        productoServicio = new Producto(1L, "Corte de Cabello", "Corte clásico de caballero", 
                                       50.0, 30, TipoProducto.SERVICIO, true, 
                                       LocalDateTime.now(), null);
        
        // Producto Físico (Duración = 0)
        productoFisico = new Producto(3L, "Shampoo", "Shampoo para el pelo", 
                                       25.0, 0, TipoProducto.FISICO, true, 
                                       LocalDateTime.now(), null); // <-- Duración 0
        
        // Producto Inactivo
        productoInactivo = new Producto(2L, "Masaje Relajante", "Masaje de 60 minutos", 
                                        80.0, 60, TipoProducto.SERVICIO, false, 
                                        LocalDateTime.now(), null);
    }

    // --- TEST: GUARDAR PRODUCTO DE SERVICIO ---
    @Test
    void testGuardarServicio() {
        when(productoRepository.save(any(Producto.class))).thenReturn(productoServicio);
        
        Producto resultado = productoService.guardar(productoServicio);
        
        assertNotNull(resultado);
        assertEquals("Corte de Cabello", resultado.getNombre());
        verify(productoRepository, times(1)).save(productoServicio);
    }
    
    // --- TEST: GUARDAR PRODUCTO FÍSICO (NUEVO TEST) ---
    @Test
    void testGuardarProductoFisicoConDuracionCero() {
        when(productoRepository.save(any(Producto.class))).thenReturn(productoFisico);
        
        Producto resultado = productoService.guardar(productoFisico);
        
        assertNotNull(resultado);
        assertEquals(TipoProducto.FISICO, resultado.getTipo());
        // Verifica que la duración 0 es aceptada
        assertEquals(0, resultado.getDuracionMinutos()); 
        verify(productoRepository, times(1)).save(productoFisico);
    }

    // --- TEST: OBTENER POR ID ---
    @Test
    void testObtenerPorIdExistente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoServicio));
        
        Optional<Producto> resultado = productoService.obtenerPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals(productoServicio.getNombre(), resultado.get().getNombre());
    }

    // --- TEST: LISTAR ACTIVOS ---
    @Test
    void testListarActivos() {
        List<Producto> lista = Arrays.asList(productoServicio, productoFisico);
        when(productoRepository.findByActivoTrue()).thenReturn(lista);
        
        List<Producto> resultado = productoService.listarActivos();
        
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findByActivoTrue();
    }
    
    // --- TEST: ACTUALIZAR PRODUCTO ---
    @Test
    void testActualizarProductoExistente() {
        // Simula la obtención del producto existente
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoServicio));
        
        // Crea un objeto con los nuevos detalles
        Producto detallesActualizados = new Producto();
        detallesActualizados.setNombre("Corte y Barba");
        detallesActualizados.setPrecio(65.0);
        detallesActualizados.setDescripcion("Corte y arreglo de barba");

        // Simula el guardado del producto actualizado
        when(productoRepository.save(any(Producto.class))).thenReturn(productoServicio);
        
        Producto resultado = productoService.actualizar(1L, detallesActualizados);
        
        // Verifica que los campos fueron actualizados
        assertEquals("Corte y Barba", resultado.getNombre());
        assertEquals(65.0, resultado.getPrecio());
        verify(productoRepository, times(1)).save(productoServicio);
    }
    
    @Test
    void testActualizarProductoNoExistenteLanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Verifica que se lanza la excepción correcta (ResponseStatusException con NOT_FOUND)
        assertThrows(ResponseStatusException.class, () -> 
            productoService.actualizar(99L, new Producto()));
        
        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
    
    // --- TEST: EXISTE PRODUCTO ACTIVO (INTEGRACIÓN) ---
    @Test
    void testExisteProductoActivo() {
        when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(productoServicio);
        when(productoRepository.findByIdAndActivoTrue(2L)).thenReturn(null);
        
        assertTrue(productoService.existeProductoActivo(1L)); // Activo
        assertFalse(productoService.existeProductoActivo(2L)); // Inactivo o no existe
    }
}