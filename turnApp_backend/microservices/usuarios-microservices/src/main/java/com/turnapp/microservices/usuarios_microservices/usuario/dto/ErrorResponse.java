package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.time.LocalDateTime;
import lombok.Builder;

/**
 * DTO para respuestas de error personalizadas
 */
@Builder
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
}
