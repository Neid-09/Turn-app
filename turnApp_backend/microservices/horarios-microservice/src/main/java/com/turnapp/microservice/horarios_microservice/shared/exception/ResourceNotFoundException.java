package com.turnapp.microservice.horarios_microservice.shared.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra en la base de datos.
 * Mapea a HTTP 404 NOT FOUND.
 * 
 * @author TurnApp Team
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
