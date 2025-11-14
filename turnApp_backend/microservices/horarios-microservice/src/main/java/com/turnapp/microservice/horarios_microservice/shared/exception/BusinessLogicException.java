package com.turnapp.microservice.horarios_microservice.shared.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 * Mapea a HTTP 409 CONFLICT.
 * 
 * Ejemplos:
 * - Intentar publicar un horario ya publicado
 * - Crear horario con fechas inválidas
 * - Solapamiento de turnos al publicar
 * 
 * @author TurnApp Team
 */
public class BusinessLogicException extends RuntimeException {
    
    public BusinessLogicException(String message) {
        super(message);
    }
    
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
