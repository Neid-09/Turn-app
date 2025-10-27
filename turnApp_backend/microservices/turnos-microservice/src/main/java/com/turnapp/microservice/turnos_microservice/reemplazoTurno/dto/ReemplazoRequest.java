package com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la creación de solicitudes de reemplazo de turno.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReemplazoRequest {
    
    @NotNull(message = "El ID de la asignación original es obligatorio")
    private Long asignacionOriginalId;
    
    @NotBlank(message = "El ID del usuario reemplazante es obligatorio")
    private String reemplazanteId;
    
    @NotNull(message = "La fecha del reemplazo es obligatoria")
    private LocalDate fecha;
    
    @NotBlank(message = "El motivo del reemplazo es obligatorio")
    private String motivo;
}
