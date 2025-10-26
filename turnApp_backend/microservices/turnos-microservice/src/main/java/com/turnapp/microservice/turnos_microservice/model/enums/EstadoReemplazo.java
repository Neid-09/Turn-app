package com.turnapp.microservice.turnos_microservice.model.enums;

/**
 * Define el estado de una solicitud de reemplazo.
 */
public enum EstadoReemplazo {
  PENDIENTE, // La solicitud está esperando aprobación.
  APROBADO, // El reemplazo ha sido aprobado.
  RECHAZADO // El reemplazo ha sido denegado.
}