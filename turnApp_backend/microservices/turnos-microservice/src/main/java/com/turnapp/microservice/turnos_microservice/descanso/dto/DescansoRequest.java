package com.turnapp.microservice.turnos_microservice.descanso.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO para el registro de descansos durante un turno.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescansoRequest {
    
    @NotNull(message = "El ID de la asignación es obligatorio")
    private Long asignacionId;
    
    @NotNull(message = "La hora de inicio del descanso es obligatoria")
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin del descanso es obligatoria")
    private LocalTime horaFin;
    
    private String tipo;
    
    private String observaciones;
}
