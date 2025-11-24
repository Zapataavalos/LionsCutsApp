package com.example.Citas.y.reservas.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.Citas.y.reservas.Model.Cita;
import com.example.Citas.y.reservas.Model.EstadoCita;
import com.example.Citas.y.reservas.Repository.CitaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CitaService citaService;

    private final LocalDateTime FUTURO = LocalDateTime.now().plusDays(1);
    
    private Cita buildCita(Long id, Long servicioId, LocalDateTime fecha) {
        return Cita.builder()
                .id(id)
                .usuarioId(10L)
                .servicioId(servicioId)
                .fechaHora(fecha)
                .duracionMinutos(60)
                .estado(EstadoCita.PENDIENTE)
                .build();
    }
    
    // --- Pruebas de Creación (Save) ---
    
    @Test
    void save_DeberiaGuardar_CuandoNoHayConflicto() {
        Cita nuevaCita = buildCita(null, 100L, FUTURO);
        
        // GIVEN: El repositorio confirma que NO hay conflicto
        when(citaRepository.existsByServicioIdAndFechaHora(anyLong(), any(LocalDateTime.class))).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenReturn(buildCita(1L, 100L, FUTURO));

        // WHEN
        Cita resultado = citaService.save(nuevaCita);

        // THEN
        assertNotNull(resultado);
        verify(citaRepository, times(1)).save(any(Cita.class)); // Verificamos que se llamó a guardar
    }

    @Test
    void save_DeberiaLanzarError_CuandoExisteConflicto() {
        Cita nuevaCita = buildCita(null, 100L, FUTURO);
        
        // GIVEN: El repositorio confirma que SI hay conflicto
        when(citaRepository.existsByServicioIdAndFechaHora(anyLong(), any(LocalDateTime.class))).thenReturn(true);

        // WHEN & THEN: Esperamos una excepción
        assertThrows(RuntimeException.class, () -> {
            citaService.save(nuevaCita);
        });
        verify(citaRepository, never()).save(any(Cita.class)); // Aseguramos que NUNCA se guardó
    }
    
    // --- Pruebas de Actualización (Update) ---

    @Test
    void update_DeberiaActualizar_CuandoNoHayConflictoExcluyendoElMismoID() {
        Long citaId = 1L;
        Cita citaExistente = buildCita(citaId, 100L, FUTURO);
        Cita detallesActualizados = buildCita(citaId, 101L, FUTURO.plusHours(1)); // Cambiando servicio y hora
        
        // Mock: Cita encontrada, NO conflicto al excluir el ID 1
        when(citaRepository.findById(citaId)).thenReturn(Optional.of(citaExistente));
        when(citaRepository.existsByServicioIdAndFechaHoraAndIdNot(anyLong(), any(LocalDateTime.class), eq(citaId))).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(i -> i.getArguments()[0]); // Simula el guardado

        // WHEN
        Cita resultado = citaService.update(citaId, detallesActualizados);

        // THEN
        assertEquals(101L, resultado.getServicioId());
        verify(citaRepository, times(1)).save(any(Cita.class));
    }
    
    @Test
    void update_DeberiaLanzarError_CuandoConflictoPersisteAlActualizar() {
        Long citaId = 1L;
        Cita citaExistente = buildCita(citaId, 100L, FUTURO);
        Cita detallesConflicto = buildCita(citaId, 200L, FUTURO); // Hora en conflicto con otra cita (ID != 1)

        // Mock: Cita encontrada, PERO SI hay conflicto al excluir el ID 1
        when(citaRepository.findById(citaId)).thenReturn(Optional.of(citaExistente));
        when(citaRepository.existsByServicioIdAndFechaHoraAndIdNot(anyLong(), any(LocalDateTime.class), eq(citaId))).thenReturn(true);

        // WHEN & THEN: Esperamos una excepción
        assertThrows(RuntimeException.class, () -> {
            citaService.update(citaId, detallesConflicto);
        });
        verify(citaRepository, never()).save(any(Cita.class)); // Aseguramos que NUNCA se guardó
    }
    
    @Test
    void update_DeberiaLanzarError_CuandoCitaNoEsEncontrada() {
        // GIVEN: ID no encontrado
        when(citaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // WHEN & THEN: Esperamos una excepción
        assertThrows(RuntimeException.class, () -> {
            citaService.update(99L, buildCita(99L, 1L, FUTURO));
        });
    }
}