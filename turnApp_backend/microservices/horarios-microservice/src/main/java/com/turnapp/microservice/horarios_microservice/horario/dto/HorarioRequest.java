package com.turnapp.microservice.horarios_microservice.horario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de request para crear o actualizar un horario.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioRequest {
    
    @NotBlank(message = "El nombre del horario es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
    
    private String descripcion;
}
