package com.turnapp.microservice.horarios_microservice.integration.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para la información de plantillas de turno del microservicio de turnos.
 * Espejo del TurnoResponse del microservicio de turnos.
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
