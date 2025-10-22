package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import lombok.Builder;

/**
 * DTO para respuestas de información del usuario autenticado
 */
@Builder
public record UsuarioInfoRes(
    String id,
    String email,
    String firstName,
    String lastName,
    String codigoEmpleado,
    String cargo,
    String numeroIdentificacion,
    String telefono,
    boolean enabled
) {
}
