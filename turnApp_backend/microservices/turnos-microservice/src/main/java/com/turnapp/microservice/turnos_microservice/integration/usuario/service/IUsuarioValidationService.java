package com.turnapp.microservice.turnos_microservice.integration.usuario.service;

/**
 * Servicio para validación de usuarios desde el microservicio de usuarios.
 * Encapsula la lógica de comunicación entre microservicios.
 * 
 * IMPORTANTE: Se usa keycloakId como identificador único del usuario.
 * 
 * @author TurnApp Team
 */
public interface IUsuarioValidationService {
    
    /**
     * Valida que un usuario exista en el sistema mediante su Keycloak ID.
     * 
     * @param keycloakId ID de Keycloak del usuario a validar
     * @return true si el usuario existe, false en caso contrario
     */
    boolean existeUsuario(String keycloakId);
    
    /**
     * Valida que un usuario exista y esté activo (enabled en Keycloak).
     * 
     * @param keycloakId ID de Keycloak del usuario a validar
     * @return true si el usuario existe y está activo
     */
    boolean existeYEstaActivo(String keycloakId);
    
    /**
     * Valida que un usuario exista y tenga el rol de aplicación requerido.
     * Útil para validar que solo EMPLEADOS puedan tener turnos asignados.
     * 
     * @param keycloakId ID de Keycloak del usuario
     * @param rolApp Rol de aplicación requerido (ej: "EMPLEADO", "ADMIN")
     * @return true si el usuario tiene el rol
     */
    boolean tieneRolApp(String keycloakId, String rolApp);
    
    /**
     * Obtiene la información básica de un usuario por su Keycloak ID.
     * Útil para obtener información adicional sin tener que validar.
     * 
     * @param keycloakId ID de Keycloak del usuario
     * @return DTO con información del usuario
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si no existe
     */
    com.turnapp.microservice.turnos_microservice.integration.usuario.client.UsuarioBasicoResponse obtenerUsuario(String keycloakId);
}
