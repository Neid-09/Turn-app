package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import lombok.Builder;

/**
 * DTO para respuestas de éxito genéricas
 */
@Builder
public record ApiResponse(
    String message,
    Object data
) {
    public static ApiResponse success(String message) {
        return ApiResponse.builder()
            .message(message)
            .build();
    }

    public static ApiResponse success(String message, Object data) {
        return ApiResponse.builder()
            .message(message)
            .data(data)
            .build();
    }
}
