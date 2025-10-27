package com.turnapp.microservice.turnos_microservice.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura estándar de respuesta de error para la API.
 * Proporciona información detallada sobre errores HTTP.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    /**
     * Timestamp del momento en que ocurrió el error.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Código de estado HTTP (ej: 404, 400, 500).
     */
    private int status;
    
    /**
     * Nombre del error HTTP (ej: "Not Found", "Bad Request").
     */
    private String error;
    
    /**
     * Mensaje descriptivo del error.
     */
    private String message;
    
    /**
     * Path de la petición que causó el error.
     */
    private String path;
    
    /**
     * Lista de errores de validación (opcional).
     * Se usa cuando hay múltiples errores en la validación de campos.
     */
    private List<String> validationErrors;
}
