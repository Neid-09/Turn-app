package com.turnapp.microservices.usuarios_microservices.usuario.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario.
 */
public class UsuarioNotFoundException extends RuntimeException {
    
    public UsuarioNotFoundException(String message) {
        super(message);
    }
    
    public UsuarioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
