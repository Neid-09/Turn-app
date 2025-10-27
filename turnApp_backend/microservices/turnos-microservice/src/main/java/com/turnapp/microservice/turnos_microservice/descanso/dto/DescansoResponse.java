package com.turnapp.microservice.turnos_microservice.descanso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para la información de descansos.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescansoResponse {
    
    private Long id;
    private Long asignacionId;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipo;
    private String observaciones;
    private LocalDateTime creadoEn;
}
