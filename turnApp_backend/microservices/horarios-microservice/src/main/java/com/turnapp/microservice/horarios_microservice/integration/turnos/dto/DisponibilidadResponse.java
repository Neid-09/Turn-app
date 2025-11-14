package com.turnapp.microservice.horarios_microservice.integration.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para la información de disponibilidad del microservicio de turnos.
 * Espejo del DisponibilidadResponse del microservicio de turnos.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadResponse {
    
    private Long id;
    private String usuarioId;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean activo;
    private LocalDateTime creadoEn;
}
