package com.turnapp.microservice.turnos_microservice.asignacionTurno.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.EstadoAsignacion;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la información de asignaciones de turno.
 * Incluye datos del turno asignado para facilitar la visualización.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionResponse {
    
    private Long id;
    private String usuarioId;
    private Long turnoId;
    private String nombreTurno;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoAsignacion estado;
    private String observaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
