package com.turnapp.microservice.horarios_microservice.horario.model;

/**
 * Enum que define el estado de una línea de detalle del horario.
 * 
 * PLANIFICADO: Detalle planificado pero aún no confirmado en turnos-microservice
 * CONFIRMADO: Asignación creada exitosamente en turnos-microservice
 * MODIFICADO: Asignación que fue modificada después de la publicación
 * CANCELADO: Asignación cancelada
 * 
 * @author TurnApp Team
 */
public enum EstadoDetalle {
    PLANIFICADO,   // Solo existe en horarios, no publicado a turnos
    CONFIRMADO,    // Publicado exitosamente a turnos
    MODIFICADO,    // Modificado post-publicación
    CANCELADO      // Cancelado
}
