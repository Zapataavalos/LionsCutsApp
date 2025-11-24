package com.example.Citas.y.reservas.Repository;
import com.example.Citas.y.reservas.Model.Cita;
import com.example.Citas.y.reservas.Model.EstadoCita;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // --- Métodos de Validación de Conflictos (CRÍTICOS) ---

    /**
     * Valida si existe alguna cita para un servicio a una hora de inicio específica.
     * Usado para la creación de nuevas citas (POST).
     */
    boolean existsByServicioIdAndFechaHora(Long servicioId, LocalDateTime fechaHora);

    /**
     * Valida si existe alguna cita en conflicto con una hora y servicio específicos, 
     * excluyendo la cita actual (su ID). Usado para la actualización (PUT).
     */
    boolean existsByServicioIdAndFechaHoraAndIdNot(Long servicioId, LocalDateTime fechaHora, Long citaIdAExcluir);

    // --- Métodos de Búsqueda Comunes ---

    /**
     * Busca todas las citas asociadas a un ID de usuario.
     */
    List<Cita> findByUsuarioId(Long usuarioId);

    /**
     * Busca todas las citas con un estado específico (PENDIENTE, CONFIRMADA, etc.).
     */
    List<Cita> findByEstado(EstadoCita estado);

    /**
     * Busca todas las citas programadas después de una fecha y hora específica 
     * (útil para listar próximas reservas).
     */
    List<Cita> findByFechaHoraAfter(LocalDateTime fechaHora);

    /**
     * Busca todas las citas de un servicio específico.
     */
    List<Cita> findByServicioId(Long servicioId);
}
