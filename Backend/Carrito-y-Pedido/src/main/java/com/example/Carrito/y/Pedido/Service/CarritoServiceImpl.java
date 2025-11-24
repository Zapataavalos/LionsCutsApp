package com.example.Carrito.y.Pedido.Service;

import com.example.Carrito.y.Pedido.Client.ProductoClient; // <--- NUEVO: CLIENTE PRODUCTOS
import com.example.Carrito.y.Pedido.DTO.ProductoDTO;       // <--- NUEVO: DTO PRODUCTOS
import com.example.Carrito.y.Pedido.Client.UsuarioClient; 
import com.example.Carrito.y.Pedido.DTO.UsuarioDTO;       
import com.example.Carrito.y.Pedido.Model.*;
import com.example.Carrito.y.Pedido.Repository.CarritoRepository;
import com.example.Carrito.y.Pedido.Repository.ItemCarritoRepository;
import com.example.Carrito.y.Pedido.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    // Inyectamos el cliente para comunicarnos con Usuarios (Puerto 8081)
    @Autowired
    private UsuarioClient usuarioClient; 

    // --- NUEVO: Inyectamos el cliente para comunicarnos con Productos (Puerto 8083) ---
    @Autowired
    private ProductoClient productoClient;
    // ----------------------------------------------------------------------------------

    // --- Lógica Principal del Carrito ---

    @Override
    public Carrito obtenerOCrearCarritoActivo(Long clienteId) {
        return carritoRepository.findByClienteIdAndEstado(clienteId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    // VALIDACIÓN USUARIO: Verificamos que el cliente exista
                    usuarioClient.obtenerUsuarioPorId(clienteId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "No se puede crear el carrito. El cliente con ID " + clienteId + " no existe."));

                    Carrito nuevoCarrito = Carrito.builder()
                            .clienteId(clienteId)
                            .estado(EstadoCarrito.ACTIVO)
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    @Override
    @Transactional
    public ItemCarrito agregarOActualizarItem(Long clienteId, Long productoId, 
                                             int cantidad, Double precioSimulado, 
                                             Integer duracionSimulada) {
        
        if (cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero.");
        }

        // --- NUEVO: VALIDACIÓN DE PRODUCTO Y PRECIO REAL ---
        // Consultamos al Microservicio de Catálogo para obtener los datos reales
        ProductoDTO productoReal = productoClient.obtenerProductoPorId(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "El producto con ID " + productoId + " no existe en el catálogo."));

        // Verificamos si el producto está activo para la venta
        if (!productoReal.isActivo()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto '" + productoReal.getNombre() + "' no está disponible actualmente.");
        }
        // ---------------------------------------------------

        Carrito carrito = obtenerOCrearCarritoActivo(clienteId);

        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);

        ItemCarrito item;

        if (itemExistente.isPresent()) {
            item = itemExistente.get();
            item.setCantidad(cantidad); 
            // Actualizamos el precio al valor real actual del catálogo
            item.setPrecioUnitario(productoReal.getPrecio());
        } else {
            item = ItemCarrito.builder()
                    .carrito(carrito)
                    .productoId(productoId)
                    .cantidad(cantidad)
                    // ¡SEGURIDAD!: Usamos el precio real del microservicio, NO el que envía Postman
                    .precioUnitario(productoReal.getPrecio()) 
                    // Usamos la duración real del microservicio
                    .duracionUnitarioMinutos(productoReal.getDuracionMinutos())
                    .build();
            
            if (carrito.getItems() == null) carrito.setItems(new java.util.ArrayList<>());
            carrito.getItems().add(item);
        }

        carritoRepository.save(carrito); 

        return itemCarritoRepository.save(item);
    }

    @Override
    @Transactional
    public void eliminarItem(Long clienteId, Long productoId) {
        Carrito carrito = obtenerOCrearCarritoActivo(clienteId);

        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ítem no encontrado en el carrito."));

        carrito.getItems().remove(item);
        itemCarritoRepository.delete(item);

        carritoRepository.save(carrito); 
    }

    @Override
    public Carrito obtenerCarritoPorId(Long carritoId) {
        return carritoRepository.findById(carritoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado."));
    }

    // --- Lógica de Pedido (Conversión) ---

    @Override
    @Transactional
    public Pedido finalizarCompra(Long clienteId) {
        
        // VALIDACIÓN USUARIO: Verificamos cliente antes de procesar
        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe."));

        Carrito carrito = obtenerOCrearCarritoActivo(clienteId);
        
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío y no puede finalizar la compra.");
        }

        Double total = calcularTotal(carrito.getId());
        
        List<ItemPedido> itemsPedido = carrito.getItems().stream()
            .map(itemCarrito -> ItemPedido.builder()
                .productoId(itemCarrito.getProductoId())
                .cantidad(itemCarrito.getCantidad())
                .precioPagado(itemCarrito.getPrecioUnitario()) 
                .duracionRegistradaMinutos(itemCarrito.getDuracionUnitarioMinutos())
                .build()
            ).collect(Collectors.toList());

        Pedido nuevoPedido = Pedido.builder()
                .clienteId(clienteId) 
                .total(total)
                .carritoOrigenId(carrito.getId())
                .estado(EstadoPedido.PENDIENTE_PAGO)
                .fechaCreacion(LocalDateTime.now())
                .items(itemsPedido)
                .build();
        
        itemsPedido.forEach(item -> item.setPedido(nuevoPedido));

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        carrito.setEstado(EstadoCarrito.COMPLETADO);
        carritoRepository.save(carrito);

        return pedidoGuardado;
    }

    @Override
    public Double calcularTotal(Long carritoId) {
        Carrito carrito = obtenerCarritoPorId(carritoId);
        if (carrito.getItems() == null) return 0.0;

        return carrito.getItems().stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();
    }

    @Override
    public Integer calcularDuracionTotalMinutos(Long carritoId) {
        Carrito carrito = obtenerCarritoPorId(carritoId);
        if (carrito.getItems() == null) return 0;

        return carrito.getItems().stream()
                .filter(item -> item.getDuracionUnitarioMinutos() != null)
                .mapToInt(item -> item.getDuracionUnitarioMinutos() * item.getCantidad())
                .sum();
    }
}