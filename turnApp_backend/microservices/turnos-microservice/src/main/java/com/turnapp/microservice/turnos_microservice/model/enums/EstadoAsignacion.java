package com.turnapp.microservice.turnos_microservice.model.enums;

/**
 * Define el ciclo de vida de un turno que ha sido asignado
 * a un usuario en una fecha específica.
 */
public enum EstadoAsignacion {
  ASIGNADO, // El turno está programado y pendiente de realizarse.
  COMPLETADO, // El turno ya se realizó.
  CANCELADO, // El turno fue cancelado (por admin o reemplazo).
  AUSENTE // El empleado no se presentó (opcional).
}
