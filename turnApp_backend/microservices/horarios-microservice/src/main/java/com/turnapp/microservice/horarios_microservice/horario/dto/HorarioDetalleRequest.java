package com.turnapp.microservice.horarios_microservice.horario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de request para crear o actualizar un detalle de horario.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDetalleRequest {
    
    @NotBlank(message = "El ID del usuario es obligatorio")
    private String usuarioId;
    
    @NotNull(message = "La fecha de asignación es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "El ID del turno es obligatorio")
    private Long turnoId;
    
    private String observaciones;
}
