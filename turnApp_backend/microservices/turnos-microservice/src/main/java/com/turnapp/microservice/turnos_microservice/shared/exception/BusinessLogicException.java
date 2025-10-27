package com.turnapp.microservice.turnos_microservice.shared.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio de la aplicación.
 * Ejemplos: solapamiento de turnos, estado inválido, validaciones de negocio.
 * Típicamente resulta en una respuesta HTTP 400 (Bad Request) o 409 (Conflict).
 * 
 * @author TurnApp Team
 */
public class BusinessLogicException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Mensaje descriptivo de la regla de negocio violada
     */
    public BusinessLogicException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
