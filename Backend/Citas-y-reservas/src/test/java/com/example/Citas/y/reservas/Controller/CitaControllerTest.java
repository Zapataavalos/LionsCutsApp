package com.example.Citas.y.reservas.Controller;

import com.example.Citas.y.reservas.Model.Cita;
import com.example.Citas.y.reservas.Model.EstadoCita;
import com.example.Citas.y.reservas.Service.CitaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitaController.class)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaService citaService;
    
    private ObjectMapper objectMapper;
    private Cita validCita;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Necesario para serializar/deserializar LocalDateTime
        
        validCita = Cita.builder()
                .id(1L)
                .usuarioId(1L)
                .servicioId(10L)
                .fechaHora(LocalDateTime.now().plusDays(5)) 
                .duracionMinutos(60)
                .estado(EstadoCita.PENDIENTE)
                .build();
    }

    // --- Pruebas de Creación (POST) ---
    
    @Test
    void crear_DeberiaRetornar201Created_CuandoEsExitoso() throws Exception {
        when(citaService.save(any(Cita.class))).thenReturn(validCita);

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCita)))
                .andExpect(status().isCreated()) // 201 CREATED
                .andExpect(jsonPath("$.id").value(1));

        verify(citaService, times(1)).save(any(Cita.class));
    }
    
    @Test
    void crear_DeberiaRetornar400BadRequest_CuandoFallaValidacionModelo() throws Exception {
        // Cita Inválida: fechaHora en el pasado (falla @Future)
        Cita invalidCita = validCita;
        invalidCita.setFechaHora(LocalDateTime.now().minusDays(1)); 
        
        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCita)))
                .andExpect(status().isBadRequest()) // 400 BAD REQUEST por @Valid
                // Verificamos que el manejador de errores de validación devuelve el campo de error
                .andExpect(jsonPath("$.fechaHora").exists()); 
        
        verify(citaService, never()).save(any(Cita.class));
    }
    
    @Test
    void crear_DeberiaRetornar409Conflict_CuandoFallaLogicaDeNegocio() throws Exception {
        // Simular la excepción de conflicto lanzada por el Service
        when(citaService.save(any(Cita.class))).thenThrow(new RuntimeException("ERROR: El servicio ya está reservado"));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCita)))
                .andExpect(status().isConflict()) // 409 CONFLICT
                .andExpect(content().string("ERROR: El servicio ya está reservado"));

        verify(citaService, times(1)).save(any(Cita.class));
    }

    // --- Pruebas de Actualización (PUT) ---

    @Test
    void actualizar_DeberiaRetornar200Ok_CuandoActualizacionEsExitosa() throws Exception {
        Cita updatedCita = validCita;
        updatedCita.setEstado(EstadoCita.CONFIRMADA);
        
        when(citaService.update(eq(1L), any(Cita.class))).thenReturn(updatedCita);

        mockMvc.perform(put("/api/citas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCita)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        verify(citaService, times(1)).update(eq(1L), any(Cita.class));
    }
    
    @Test
    void actualizar_DeberiaRetornar400BadRequest_CuandoFallaConflictoDeActualizacion() throws Exception {
        // Simular que el Service lanza error de conflicto
        when(citaService.update(eq(1L), any(Cita.class)))
            .thenThrow(new RuntimeException("ERROR: La nueva fecha/hora entra en conflicto"));

        mockMvc.perform(put("/api/citas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCita)))
                .andExpect(status().isBadRequest()) // 400 BAD REQUEST (Error de negocio, no encontrado o conflicto)
                .andExpect(content().string("ERROR: La nueva fecha/hora entra en conflicto"));
    }
}