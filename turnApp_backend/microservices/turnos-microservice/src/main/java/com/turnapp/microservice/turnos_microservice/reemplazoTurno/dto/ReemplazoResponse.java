package com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.turnapp.microservice.turnos_microservice.reemplazoTurno.model.EstadoReemplazo;

/**
 * DTO de respuesta para la información de reemplazos de turno.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReemplazoResponse {
    
    private Long id;
    private Long asignacionOriginalId;
    private String reemplazanteId;
    private Long aprobadorId;
    private String motivo;
    private LocalDate fecha;
    private EstadoReemplazo estado;
    private LocalDateTime creadoEn;
}
