package com.turnapp.microservice.horarios_microservice.horario.dto;

import com.turnapp.microservice.horarios_microservice.integration.turnos.dto.AsignacionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO de response para la vista consolidada de un horario.
 * 
 * Combina información del horario local con asignaciones reales
 * del microservicio de turnos para generar una vista completa.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioConsolidadoResponse {
    
    /**
     * Información básica del horario.
     */
    private HorarioResponse horario;
    
    /**
     * Asignaciones reales obtenidas del microservicio de turnos.
     * Organizadas por fecha para facilitar la visualización de calendario.
     * 
     * Estructura: Map<"2025-12-01", List<AsignacionResponse>>
     */
    private Map<LocalDate, List<AsignacionResponse>> asignacionesPorFecha;
    
    /**
     * Estadísticas del horario consolidado.
     */
    private EstadisticasHorario estadisticas;
    
    /**
     * Clase interna para estadísticas.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstadisticasHorario {
        
        /**
         * Total de asignaciones planificadas en el horario.
         */
        private Integer totalPlanificadas;
        
        /**
         * Total de asignaciones confirmadas en turnos.
         */
        private Integer totalConfirmadas;
        
        /**
         * Total de asignaciones completadas.
         */
        private Integer totalCompletadas;
        
        /**
         * Total de asignaciones canceladas.
         */
        private Integer totalCanceladas;
        
        /**
         * Porcentaje de sincronización con turnos-microservice.
         */
        private Double porcentajeSincronizacion;
    }
}
