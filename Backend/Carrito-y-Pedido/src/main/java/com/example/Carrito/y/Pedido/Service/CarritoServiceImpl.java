package com.example.Carrito.y.Pedido.Service;

import com.example.Carrito.y.Pedido.Client.ProductoClient;
import com.example.Carrito.y.Pedido.Client.UsuarioClient;
import com.example.Carrito.y.Pedido.DTO.ProductoDTO;
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

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private ProductoClient productoClient;

    // ======================================================
    // CARRITO
    // ======================================================

    @Override
    public Carrito obtenerOCrearCarritoActivo(Long clienteId) {
        return carritoRepository.findByClienteIdAndEstado(clienteId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {

                    // Validar que el usuario exista
                    usuarioClient.obtenerUsuarioPorId(clienteId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "No existe el cliente con ID " + clienteId
                            ));

                    Carrito nuevo = Carrito.builder()
                            .clienteId(clienteId)
                            .estado(EstadoCarrito.ACTIVO)
                            .fechaCreacion(LocalDateTime.now())
                            .build();

                    return carritoRepository.save(nuevo);
                });
    }

    @Override
    @Transactional
    public ItemCarrito agregarOActualizarItem(
            Long clienteId,
            Long productoId,
            int cantidad,
            Double precioSimulado,
            Integer duracionSimulada
    ) {

        if (cantidad <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a cero"
            );
        }

        // Obtener producto REAL desde Catálogo
        ProductoDTO producto = productoClient.obtenerProductoPorId(productoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no existe en el catálogo"
                ));

        if (!producto.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El producto no está disponible"
            );
        }

        Carrito carrito = obtenerOCrearCarritoActivo(clienteId);

        Optional<ItemCarrito> existente =
                itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);

        ItemCarrito item;

        if (existente.isPresent()) {
            item = existente.get();
            item.setCantidad(cantidad);
            item.setPrecioUnitario(producto.getPrecio());
            item.setDuracionUnitarioMinutos(producto.getDuracionMinutos());
        } else {
            item = ItemCarrito.builder()
                    .carrito(carrito)
                    .productoId(productoId)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecio())
                    .duracionUnitarioMinutos(producto.getDuracionMinutos())
                    .build();

            if (carrito.getItems() == null) {
                carrito.setItems(new java.util.ArrayList<>());
            }

            carrito.getItems().add(item);
        }

        carritoRepository.save(carrito);
        return itemCarritoRepository.save(item);
    }

    @Override
    @Transactional
    public void eliminarItem(Long clienteId, Long productoId) {

        Carrito carrito = obtenerOCrearCarritoActivo(clienteId);

        ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ítem no encontrado en el carrito"
                ));

        carrito.getItems().remove(item);
        itemCarritoRepository.delete(item);
        carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorId(Long carritoId) {
        return carritoRepository
                .findByIdAndEstado(carritoId, EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carrito activo no encontrado"
                ));
    }

    // ======================================================
    // PEDIDO
    // ======================================================

    @Override
    @Transactional
    public Pedido finalizarCompra(Long clienteId) {

        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(clienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El cliente no existe"
                ));

        Carrito carrito = obtenerOCrearCarritoActivo(clienteId);

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El carrito está vacío"
            );
        }

        Double total = calcularTotal(carrito.getId());

        List<ItemPedido> itemsPedido = carrito.getItems().stream()
                .map(i -> ItemPedido.builder()
                        .productoId(i.getProductoId())
                        .cantidad(i.getCantidad())
                        .precioPagado(i.getPrecioUnitario())
                        .duracionRegistradaMinutos(i.getDuracionUnitarioMinutos())
                        .build()
                ).collect(Collectors.toList());

        Pedido pedido = Pedido.builder()
                .clienteId(clienteId)
                .total(total)
                .estado(EstadoPedido.PENDIENTE_PAGO)
                .carritoOrigenId(carrito.getId())
                .fechaCreacion(LocalDateTime.now())
                .items(itemsPedido)
                .build();

        itemsPedido.forEach(i -> i.setPedido(pedido));

        Pedido guardado = pedidoRepository.save(pedido);

        carrito.setEstado(EstadoCarrito.COMPLETADO);
        carritoRepository.save(carrito);

        return guardado;
    }

    @Override
    public Double calcularTotal(Long carritoId) {
        Carrito carrito = obtenerCarritoPorId(carritoId);

        if (carrito.getItems() == null) return 0.0;

        return carrito.getItems().stream()
                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
                .sum();
    }

    @Override
    public Integer calcularDuracionTotalMinutos(Long carritoId) {
        Carrito carrito = obtenerCarritoPorId(carritoId);

        if (carrito.getItems() == null) return 0;

        return carrito.getItems().stream()
                .mapToInt(i -> i.getDuracionUnitarioMinutos() * i.getCantidad())
                .sum();
    }
}
