package com.example.Catalogo.De.Productos.Service;


import java.util.List;
import java.util.Optional;

import com.example.Catalogo.De.Productos.Model.Producto;
import com.example.Catalogo.De.Productos.Model.TipoProducto;

public interface ProductoService {

    // CRUD Básico
    List<Producto> listarTodos();
    Optional<Producto> obtenerPorId(Long id);
    Producto guardar(Producto producto);
    void eliminar(Long id);

    // Lógica de Negocio
    Producto actualizar(Long id, Producto productoDetalles);
    
    // Búsquedas del Catálogo
    List<Producto> listarActivos();
    List<Producto> listarPorTipo(TipoProducto tipo);
    
    // Método para la integración con otros microservicios (Citas)
    boolean existeProductoActivo(Long id);
}