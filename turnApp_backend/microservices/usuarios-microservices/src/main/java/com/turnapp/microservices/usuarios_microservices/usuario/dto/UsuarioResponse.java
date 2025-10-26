package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.time.LocalDate;

import com.turnapp.microservices.usuarios_microservices.usuario.model.RolApp;

import lombok.Builder;

/**
 * DTO para respuesta de información completa del usuario.
 * Combina datos de Keycloak y de la base de datos local.
 */
@Builder
public record UsuarioResponse(
    // ID interno de la BD
    String id,
    
    // ID de Keycloak
    String keycloakId,
    
    // Datos de Keycloak
    String email,
    String firstName,
    String lastName,
    Boolean enabled,
    Boolean emailVerified,
    
    // Datos laborales (BD local)
    String codigoEmpleado,
    String cargo,
    LocalDate fechaContratacion,
    RolApp rolApp,
    String numeroIdentificacion,
    String telefono
) {}
