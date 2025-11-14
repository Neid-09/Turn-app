package com.turnapp.microservice.horarios_microservice.shared.exception;

/**
 * Excepción lanzada cuando falla la validación de datos.
 * Mapea a HTTP 400 BAD REQUEST.
 * 
 * @author TurnApp Team
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
