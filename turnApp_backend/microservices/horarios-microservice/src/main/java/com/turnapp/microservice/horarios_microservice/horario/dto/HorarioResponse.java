package com.turnapp.microservice.horarios_microservice.horario.dto;

import com.turnapp.microservice.horarios_microservice.horario.model.EstadoHorario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de response para la información de un horario.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioResponse {
    
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoHorario estado;
    private String descripcion;
    private String creadoPor;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime publicadoEn;
    
    /**
     * Cantidad de detalles (asignaciones) en el horario.
     */
    private Integer cantidadDetalles;
    
    /**
     * Lista de detalles (opcional, solo cuando se solicita detalle completo).
     */
    private List<HorarioDetalleResponse> detalles;
}
