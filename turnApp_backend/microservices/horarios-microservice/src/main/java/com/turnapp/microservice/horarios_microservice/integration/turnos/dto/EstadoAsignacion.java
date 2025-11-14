package com.turnapp.microservice.horarios_microservice.integration.turnos.dto;

/**
 * Define el ciclo de vida de un turno asignado.
 * Espejo del EstadoAsignacion del microservicio de turnos.
 * 
 * @author TurnApp Team
 */
public enum EstadoAsignacion {
    ASIGNADO,    // El turno está programado y pendiente de realizarse
    COMPLETADO,  // El turno ya se realizó
    CANCELADO,   // El turno fue cancelado
    AUSENTE      // El empleado no se presentó
}
