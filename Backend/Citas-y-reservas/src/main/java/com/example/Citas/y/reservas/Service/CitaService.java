package com.example.Citas.y.reservas.Service;



import com.example.Citas.y.reservas.Model.Cita;
import com.example.Citas.y.reservas.Repository.CitaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    // --- CRUD Básico ---

    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    public Optional<Cita> findById(Long id) {
        return citaRepository.findById(id);
    }
    
    public void deleteById(Long id) {
        citaRepository.deleteById(id);
    }

    // --- Lógica de Negocio: Creación ---

    public Cita save(Cita cita) {
        // 1. Validar que no haya otra cita para este servicio a la misma hora de inicio
        if (citaRepository.existsByServicioIdAndFechaHora(cita.getServicioId(), cita.getFechaHora())) {
            throw new RuntimeException("ERROR: El servicio con ID " + cita.getServicioId() + 
                                     " ya está reservado para la hora de inicio: " + cita.getFechaHora());
        }
        
        // El @PrePersist en el modelo se encarga de setear fechaCreacion
        return citaRepository.save(cita);
    }
    
    // --- Lógica de Negocio: Actualización ---
    
    public Cita update(Long id, Cita citaDetails) {
        // Buscamos la cita existente
        return citaRepository.findById(id).map(citaExistente -> {
            
            // 1. Validación de conflicto: 
            // Buscamos conflictos, pero excluimos la cita que estamos actualizando (usamos el ID)
            if (citaRepository.existsByServicioIdAndFechaHoraAndIdNot(
                    citaDetails.getServicioId(), 
                    citaDetails.getFechaHora(), 
                    id)) {
                
                throw new RuntimeException("ERROR: La nueva fecha/hora entra en conflicto con otra reserva.");
            }
            
            // 2. Aplicar cambios a la entidad existente
            citaExistente.setUsuarioId(citaDetails.getUsuarioId());
            citaExistente.setServicioId(citaDetails.getServicioId());
            citaExistente.setFechaHora(citaDetails.getFechaHora());
            citaExistente.setDuracionMinutos(citaDetails.getDuracionMinutos());
            citaExistente.setEstado(citaDetails.getEstado());
            citaExistente.setNotas(citaDetails.getNotas());
            
            // El método onUpdate() en el modelo se encarga de setear fechaActualizacion
            return citaRepository.save(citaExistente);
            
        }).orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
    }
}