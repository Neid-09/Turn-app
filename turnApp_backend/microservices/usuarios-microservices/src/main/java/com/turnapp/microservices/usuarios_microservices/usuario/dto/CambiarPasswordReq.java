package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO para cambiar la contraseña de un usuario.
 */
@Builder
public record CambiarPasswordReq(
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial"
    )
    String nuevaPassword,
    
    Boolean temporal
) {}
