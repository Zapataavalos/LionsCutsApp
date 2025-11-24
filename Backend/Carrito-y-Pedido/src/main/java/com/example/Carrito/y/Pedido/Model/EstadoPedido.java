package com.example.Carrito.y.Pedido.Model;

public enum EstadoPedido {
    PENDIENTE_PAGO, // El carrito se convirtió en pedido, esperando confirmación de pago
    PAGADO,         // Pago exitoso, la orden está confirmada
    EN_PROCESO,     // La orden está siendo preparada
    ENVIADO,        // La orden salió para entrega/servicio
    ENTREGADO,      // La orden fue completada
    CANCELADO,      // La orden fue cancelada por el cliente o el sistema
    REEMBOLSADO     // La orden fue devuelta y el dinero reintegrado
}