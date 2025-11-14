package com.turnapp.microservice.horarios_microservice.integration.turnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la creación de asignaciones de turno en el microservicio de turnos.
 * Espejo del AsignacionRequest del microservicio de turnos.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionRequest {
    
    @NotBlank(message = "El ID del usuario es obligatorio")
    private String usuarioId;
    
    @NotNull(message = "El ID del turno es obligatorio")
    private Long turnoId;
    
    @NotNull(message = "La fecha de asignación es obligatoria")
    private LocalDate fecha;
    
    private String observaciones;
}
