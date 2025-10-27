package com.turnapp.microservice.turnos_microservice.turno.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import com.turnapp.microservice.turnos_microservice.turno.model.EstadoTurno;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la información de plantillas de turno.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoResponse {
    
    private Long id;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer duracionTotal;
    private EstadoTurno estado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
