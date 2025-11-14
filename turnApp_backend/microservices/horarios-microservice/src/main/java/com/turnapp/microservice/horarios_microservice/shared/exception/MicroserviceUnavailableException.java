package com.turnapp.microservice.horarios_microservice.shared.exception;

import lombok.Getter;

/**
 * Excepción lanzada cuando un microservicio externo no está disponible o falla.
 * Mapea a HTTP 503 SERVICE UNAVAILABLE.
 * 
 * Ejemplos:
 * - Microservicio de turnos no responde
 * - Timeout en comunicación Feign
 * - Error de conexión con servicio externo
 * 
 * @author TurnApp Team
 */
@Getter
public class MicroserviceUnavailableException extends RuntimeException {
    
    private final String microserviceName;
    
    public MicroserviceUnavailableException(String microserviceName, String message) {
        super(message);
        this.microserviceName = microserviceName;
    }
    
    public MicroserviceUnavailableException(String microserviceName, String message, Throwable cause) {
        super(message, cause);
        this.microserviceName = microserviceName;
    }
}
