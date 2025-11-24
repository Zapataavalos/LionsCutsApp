package com.example.Carrito.y.Pedido.Model;

public enum EstadoCarrito {
    ACTIVO,     // Listo para añadir/quitar ítems
    PENDIENTE,  // En proceso de pago
    COMPLETADO, // Convertido a Pedido
    CANCELADO   // Abandonado o cancelado
}
