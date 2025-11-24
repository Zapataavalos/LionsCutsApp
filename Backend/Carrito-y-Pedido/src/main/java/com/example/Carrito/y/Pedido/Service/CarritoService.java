package com.example.Carrito.y.Pedido.Service;

import com.example.Carrito.y.Pedido.Model.Carrito;
import com.example.Carrito.y.Pedido.Model.ItemCarrito;
import com.example.Carrito.y.Pedido.Model.Pedido;

public interface CarritoService {

    /**
     * Busca o crea el carrito ACTIVO para un cliente específico.
     * Si no existe, crea uno nuevo.
     * @param clienteId ID del cliente.
     * @return El carrito activo.
     */
    Carrito obtenerOCrearCarritoActivo(Long clienteId);

    /**
     * Agrega un producto al carrito o actualiza su cantidad si ya existe.
     * @param clienteId ID del cliente.
     * @param productoId ID del producto del Catálogo.
     * @param cantidad Cantidad a añadir/establecer.
     * @param precioUnitario Precio del catálogo en ese momento.
     * @param duracionUnitarioMinutos Duración del producto/servicio.
     * @return El ItemCarrito añadido o actualizado.
     */
    ItemCarrito agregarOActualizarItem(Long clienteId, Long productoId, 
                                     int cantidad, Double precioUnitario, 
                                     Integer duracionUnitarioMinutos);

    /**
     * Elimina completamente un ítem (producto) del carrito.
     * @param clienteId ID del cliente.
     * @param productoId ID del producto a eliminar.
     */
    void eliminarItem(Long clienteId, Long productoId);

    /**
     * Obtiene el carrito por su ID, asegurando que esté activo.
     * @param carritoId ID del carrito.
     * @return Carrito activo.
     */
    Carrito obtenerCarritoPorId(Long carritoId);

    /**
     * Procesa la compra: convierte el Carrito ACTIVO en una entidad Pedido.
     * Cambia el estado del carrito a COMPLETADO y crea el Pedido.
     * @param clienteId ID del cliente que finaliza la compra.
     * @return La entidad Pedido creada.
     */
    Pedido finalizarCompra(Long clienteId);
    
    /**
     * Obtiene el total monetario actual del carrito.
     * @param carritoId ID del carrito.
     * @return El total calculado.
     */
    Double calcularTotal(Long carritoId);
    
    /**
     * Obtiene el tiempo total de duración de los servicios en el carrito.
     * @param carritoId ID del carrito.
     * @return El total de minutos de duración.
     */
    Integer calcularDuracionTotalMinutos(Long carritoId);
}
