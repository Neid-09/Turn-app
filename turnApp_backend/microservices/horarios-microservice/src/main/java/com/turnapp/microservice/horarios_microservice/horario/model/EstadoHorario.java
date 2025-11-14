package com.turnapp.microservice.horarios_microservice.horario.model;

/**
 * Enum que define los estados del ciclo de vida de un horario.
 * 
 * BORRADOR: Horario en proceso de creación, aún no publicado
 * PUBLICADO: Horario publicado pero aún no vigente (fecha futura)
 * ACTIVO: Horario vigente actualmente
 * FINALIZADO: Horario cuyo período ya finalizó
 * CANCELADO: Horario cancelado antes de su finalización
 * 
 * @author TurnApp Team
 */
public enum EstadoHorario {
    BORRADOR,      // En edición, no visible para empleados
    PUBLICADO,     // Visible pero no vigente aún
    ACTIVO,        // Vigente actualmente
    FINALIZADO,    // Período completado
    CANCELADO      // Cancelado administrativamente
}
