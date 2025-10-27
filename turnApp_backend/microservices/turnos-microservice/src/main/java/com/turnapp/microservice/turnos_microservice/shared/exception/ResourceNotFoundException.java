package com.turnapp.microservice.turnos_microservice.shared.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra en la base de datos.
 * Típicamente resulta en una respuesta HTTP 404 (Not Found).
 * 
 * @author TurnApp Team
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Mensaje descriptivo del recurso no encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
