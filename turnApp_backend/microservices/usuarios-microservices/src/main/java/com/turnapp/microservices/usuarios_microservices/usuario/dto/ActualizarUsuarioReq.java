package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.time.LocalDate;

import com.turnapp.microservices.usuarios_microservices.usuario.model.RolApp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO para actualizar información de un usuario existente.
 * Todos los campos son opcionales (se actualizan solo los proporcionados).
 */
@Builder
public record ActualizarUsuarioReq(
    // Datos de Keycloak (opcionales)
    @Email(message = "El email debe tener un formato válido")
    String email,
    
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    String firstName,
    
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    String lastName,
    
    Boolean enabled,
    
    // Datos laborales (opcionales)
    String cargo,
    
    LocalDate fechaContratacion,
    
    RolApp rolApp,
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    String telefono
) {}
