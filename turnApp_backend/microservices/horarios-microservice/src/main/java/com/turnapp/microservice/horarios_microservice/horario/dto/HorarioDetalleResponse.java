package com.turnapp.microservice.horarios_microservice.horario.dto;

import com.turnapp.microservice.horarios_microservice.horario.model.EstadoDetalle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de response para la información de un detalle de horario.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDetalleResponse {
    
    private Long id;
    private Long horarioId;
    private String usuarioId;
    private LocalDate fecha;
    private Long turnoId;
    private String nombreTurno;
    private Long asignacionId;
    private EstadoDetalle estado;
    private String observaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime confirmadoEn;
}
