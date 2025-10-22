package com.turnapp.microservices.usuarios_microservices.usuario.exception;

/**
 * Excepción lanzada cuando ya existe un usuario con los mismos datos únicos.
 */
public class UsuarioDuplicadoException extends RuntimeException {
    
    public UsuarioDuplicadoException(String message) {
        super(message);
    }
    
    public UsuarioDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
