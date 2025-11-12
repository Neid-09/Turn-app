package com.turnapp.microservice.turnos_microservice.disponibilidad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un usuario disponible con información de su preferencia horaria.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDisponibleResponse {
    
    /**
     * ID de Keycloak del usuario.
     */
    private String keycloakId;
    
    /**
     * Nombre completo del usuario.
     */
    private String nombreCompleto;
    
    /**
     * Código de empleado.
     */
    private String codigoEmpleado;
    
    /**
     * Rol del usuario en la aplicación.
     */
    private String rolApp;
    
    /**
     * Indica si el usuario tiene preferencias horarias configuradas.
     */
    private boolean tienePreferencias;
    
    /**
     * Indica si el horario solicitado está dentro de las preferencias del usuario.
     * - true: Está dentro de preferencias o no tiene preferencias configuradas
     * - false: Está fuera de preferencias configuradas
     */
    private boolean cumplePreferencias;
    
    /**
     * Mensaje descriptivo sobre la disponibilidad del usuario.
     */
    private String mensaje;
}
