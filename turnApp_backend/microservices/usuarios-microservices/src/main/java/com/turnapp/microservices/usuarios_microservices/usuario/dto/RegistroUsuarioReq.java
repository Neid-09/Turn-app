package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.time.LocalDate;

import com.turnapp.microservices.usuarios_microservices.usuario.model.RolApp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegistroUsuarioReq(
    /* Datos básicos para Keycloak */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username solo puede contener letras, números, puntos, guiones y guiones bajos")
    String username,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    String email,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    String firstName,
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    String lastName,
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial"
    )
    String password,
    
    /* Informacion del empleado */
    @NotBlank(message = "El código de empleado es obligatorio")
    String codigoEmpleado,
    
    @NotBlank(message = "El cargo es obligatorio")
    String cargo,
    
    @NotNull(message = "La fecha de contratación es obligatoria")
    @Past(message = "La fecha de contratación debe ser en el pasado")
    LocalDate fechaContratacion,
    
    @NotNull(message = "El rol de la aplicación es obligatorio")
    RolApp rolApp,
    
    @NotBlank(message = "El número de identificación es obligatorio")
    String numeroIdentificacion,
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    String telefono
) {}
