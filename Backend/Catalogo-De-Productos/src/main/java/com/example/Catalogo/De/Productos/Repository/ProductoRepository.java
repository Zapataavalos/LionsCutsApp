package com.example.Catalogo.De.Productos.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Catalogo.De.Productos.Model.Producto;
import com.example.Catalogo.De.Productos.Model.TipoProducto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // --- Métodos de Búsqueda de Lógica de Negocio ---
    
    /**
     * Encuentra todos los productos cuyo campo 'activo' sea verdadero.
     * Esto es crucial para mostrar solo el catálogo disponible al cliente.
     */
    List<Producto> findByActivoTrue();

    /**
     * Encuentra todos los elementos de un TipoProducto específico (SERVICIO, RECURSO, FISICO).
     */
    List<Producto> findByTipo(TipoProducto tipo);

    /**
     * Busca productos por nombre, útil para el motor de búsqueda del catálogo.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Encuentra un producto por su ID y verifica que esté activo. 
     * Este es el método que usaría el microservicio de Citas para validar si 
     * el servicio o recurso que se quiere reservar existe y está disponible.
     */
    Producto findByIdAndActivoTrue(Long id);
}
