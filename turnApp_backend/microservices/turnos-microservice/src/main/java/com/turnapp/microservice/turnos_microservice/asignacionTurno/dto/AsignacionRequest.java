package com.turnapp.microservice.turnos_microservice.asignacionTurno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la creación de asignaciones de turno.
 * Representa la solicitud de asignar un turno a un usuario en una fecha específica.
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
