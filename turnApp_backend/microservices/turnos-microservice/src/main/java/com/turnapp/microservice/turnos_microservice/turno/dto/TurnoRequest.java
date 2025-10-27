package com.turnapp.microservice.turnos_microservice.turno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import com.turnapp.microservice.turnos_microservice.turno.model.EstadoTurno;

/**
 * DTO para la creación y actualización de plantillas de turno.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoRequest {
    
    @NotBlank(message = "El nombre del turno es obligatorio")
    private String nombre;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
    
    private Integer duracionTotal;
    
    @NotNull(message = "El estado del turno es obligatorio")
    private EstadoTurno estado;
}
