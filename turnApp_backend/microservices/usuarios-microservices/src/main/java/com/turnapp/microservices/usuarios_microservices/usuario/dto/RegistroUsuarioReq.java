package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.time.LocalDate;

import com.turnapp.microservices.usuarios_microservices.usuario.model.RolApp;

import lombok.Builder;

@Builder
public record RegistroUsuarioReq(
    /* Datos básicos para Keycloak */
    String email,
    String firstName,
    String lastName,
    String password,
    /* Informacion del empleado */
    String codigoEmpleado,
    String cargo,
    LocalDate fechaContratacion,
    RolApp rolApp,
    String numeroIdentificacion,
    String telefono
) {}
