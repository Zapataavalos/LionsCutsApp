package com.example.Catalogo.De.Productos.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.Catalogo.De.Productos.Model.Producto;
import com.example.Catalogo.De.Productos.Model.TipoProducto;
import com.example.Catalogo.De.Productos.Repository.ProductoRepository;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // --- CRUD Básico ---

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto guardar(Producto producto) {
        // La validación de unicidad de nombre (si es necesaria) se podría añadir aquí
        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(Long id) {
        // En un catálogo real, no se borra, se marca como 'activo = false'
        // Pero para el CRUD básico, usaremos el método estándar.
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto con ID " + id + " no encontrado"));
        productoRepository.delete(producto);
    }

    // --- Lógica de Negocio ---

    @Override
    public Producto actualizar(Long id, Producto productoDetalles) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto con ID " + id + " no encontrado"));

        // Actualiza solo los campos que pueden ser modificados
        productoExistente.setNombre(productoDetalles.getNombre());
        productoExistente.setDescripcion(productoDetalles.getDescripcion());
        productoExistente.setPrecio(productoDetalles.getPrecio());
        productoExistente.setDuracionMinutos(productoDetalles.getDuracionMinutos());
        productoExistente.setTipo(productoDetalles.getTipo());
        productoExistente.setActivo(productoDetalles.isActivo()); // Permitimos cambiar el estado de activación

        return productoRepository.save(productoExistente);  
    }

    // --- Búsquedas Especializadas ---

    @Override
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    @Override
    public List<Producto> listarPorTipo(TipoProducto tipo) {
        return productoRepository.findByTipo(tipo);
    }
    
    // --- Método para Microservicios (Integración) ---

    @Override
    public boolean existeProductoActivo(Long id) {
        // Si findByIdAndActivoTrue(id) devuelve un Producto, entonces existe y está activo.
        return productoRepository.findByIdAndActivoTrue(id) != null;
    }
}