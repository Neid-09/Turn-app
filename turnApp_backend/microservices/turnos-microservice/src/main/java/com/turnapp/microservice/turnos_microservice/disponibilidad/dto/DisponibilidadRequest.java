package com.turnapp.microservice.turnos_microservice.disponibilidad.dto;

import com.turnapp.microservice.turnos_microservice.disponibilidad.model.DiaSemana;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO para la creación y actualización de reglas de disponibilidad.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadRequest {
    
    @NotBlank(message = "El ID del usuario es obligatorio")
    private String usuarioId;
    
    @NotNull(message = "El día de la semana es obligatorio")
    private DiaSemana diaSemana;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
    
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}
