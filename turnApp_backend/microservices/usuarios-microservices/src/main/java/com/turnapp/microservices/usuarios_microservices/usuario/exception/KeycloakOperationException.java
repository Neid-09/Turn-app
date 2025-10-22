package com.turnapp.microservices.usuarios_microservices.usuario.exception;

/**
 * Excepción lanzada cuando falla una operación con Keycloak.
 */
public class KeycloakOperationException extends RuntimeException {
    
    private final int statusCode;
    
    public KeycloakOperationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public KeycloakOperationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
