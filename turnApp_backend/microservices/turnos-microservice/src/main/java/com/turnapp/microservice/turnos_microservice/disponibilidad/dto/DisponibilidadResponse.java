package com.turnapp.microservice.turnos_microservice.disponibilidad.dto;

import com.turnapp.microservice.turnos_microservice.disponibilidad.model.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para la información de disponibilidad.
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
