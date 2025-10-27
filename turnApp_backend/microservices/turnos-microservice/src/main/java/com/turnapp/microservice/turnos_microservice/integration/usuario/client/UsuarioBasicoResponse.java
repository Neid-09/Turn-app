package com.turnapp.microservice.turnos_microservice.integration.usuario.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado con la información esencial de un usuario.
 * Contiene únicamente los campos necesarios para validaciones y logging
 * en el microservicio de turnos.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioBasicoResponse {
    
    /**
     * ID de Keycloak - Identificador único del usuario.
     * Este ID se comparte entre Keycloak y las bases de datos de ambos microservicios.
     */
    private String keycloakId;
    
    /**
     * Indica si el usuario está habilitado/activo en Keycloak.
     * Se usa para validar que el usuario puede ser asignado a turnos.
     */
    private Boolean enabled;
    
    /**
     * Rol del usuario en la aplicación (ej: EMPLEADO, ADMIN, SUPERVISOR).
     * Se usa para validaciones de permisos cuando sea necesario.
     */
    private String rolApp;
    
    // Datos para logging y auditoría
    private String firstName;
    private String lastName;
    private String codigoEmpleado;
}
