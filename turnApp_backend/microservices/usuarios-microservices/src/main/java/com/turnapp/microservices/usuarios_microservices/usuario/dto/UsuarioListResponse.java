package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.util.UUID;

import com.turnapp.microservices.usuarios_microservices.usuario.model.RolApp;

import lombok.Builder;

/**
 * DTO simplificado para listar usuarios (sin todos los detalles).
 */
@Builder
public record UsuarioListResponse(
    UUID id,
    String keycloakId,
    String email,
    String firstName,
    String lastName,
    String codigoEmpleado,
    String cargo,
    RolApp rolApp,
    Boolean enabled
) {}
