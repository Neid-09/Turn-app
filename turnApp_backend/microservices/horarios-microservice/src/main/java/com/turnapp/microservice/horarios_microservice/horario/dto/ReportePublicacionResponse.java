package com.turnapp.microservice.horarios_microservice.horario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de response para el reporte de publicación de un horario.
 * 
 * Contiene información sobre el resultado de publicar un horario,
 * incluyendo asignaciones exitosas y fallidas.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportePublicacionResponse {
    
    /**
     * ID del horario publicado.
     */
    private Long horarioId;
    
    /**
     * Nombre del horario publicado.
     */
    private String nombreHorario;
    
    /**
     * Total de detalles procesados.
     */
    private Integer totalProcesados;
    
    /**
     * Total de asignaciones creadas exitosamente.
     */
    private Integer totalExitosos;
    
    /**
     * Total de asignaciones que fallaron.
     */
    private Integer totalFallidos;
    
    /**
     * Lista de asignaciones creadas exitosamente.
     */
    @Builder.Default
    private List<AsignacionExitosa> asignacionesExitosas = new ArrayList<>();
    
    /**
     * Lista de asignaciones que fallaron con su motivo de error.
     */
    @Builder.Default
    private List<AsignacionFallida> asignacionesFallidas = new ArrayList<>();
    
    /**
     * Información sobre una asignación exitosa.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AsignacionExitosa {
        private Long detalleId;
        private Long asignacionId;
        private String usuarioId;
        private String fecha;
        private String nombreTurno;
    }
    
    /**
     * Información sobre una asignación fallida.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AsignacionFallida {
        private Long detalleId;
        private String usuarioId;
        private String fecha;
        private Long turnoId;
        private String motivoError;
    }
    
    /**
     * Verifica si la publicación fue completamente exitosa.
     * 
     * @return true si no hubo fallos
     */
    public boolean esExitosaCompleta() {
        return totalFallidos == 0;
    }
    
    /**
     * Verifica si hubo fallos parciales.
     * 
     * @return true si hubo algunos éxitos y algunos fallos
     */
    public boolean esExitosaParcial() {
        return totalExitosos > 0 && totalFallidos > 0;
    }
}
