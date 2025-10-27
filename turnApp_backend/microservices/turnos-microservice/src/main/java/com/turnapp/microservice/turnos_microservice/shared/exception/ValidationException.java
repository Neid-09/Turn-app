package com.turnapp.microservice.turnos_microservice.shared.exception;

/**
 * Excepción lanzada cuando falla la validación de datos de entrada.
 * Típicamente resulta en una respuesta HTTP 400 (Bad Request).
 * 
 * @author TurnApp Team
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Mensaje descriptivo del error de validación
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
