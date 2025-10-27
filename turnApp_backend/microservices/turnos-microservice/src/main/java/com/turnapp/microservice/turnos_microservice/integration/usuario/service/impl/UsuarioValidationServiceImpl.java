package com.turnapp.microservice.turnos_microservice.integration.usuario.service.impl;

import com.turnapp.microservice.turnos_microservice.integration.usuario.client.UsuarioBasicoResponse;
import com.turnapp.microservice.turnos_microservice.integration.usuario.client.UsuarioClient;
import com.turnapp.microservice.turnos_microservice.integration.usuario.service.IUsuarioValidationService;
import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de validación de usuarios.
 * Maneja la comunicación con el microservicio de usuarios mediante Feign Client.
 * 
 * IMPORTANTE: Se usa keycloakId como identificador único del usuario,
 * ya que es el mismo en Keycloak y en la BD del microservicio de usuarios.
 * 
 * Estrategias implementadas:
 * - Manejo de excepciones de Feign
 * - Logging detallado de todas las llamadas
 * - Modo degradado configurable para tolerancia a fallos
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UsuarioValidationServiceImpl implements IUsuarioValidationService {
        
    private final UsuarioClient usuarioClient;
    
    @Override
    public boolean existeUsuario(String keycloakId) {
        log.debug("Validando existencia de usuario con Keycloak ID: {}", keycloakId);
        
        // Validación básica de formato antes de hacer la llamada
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.error("Keycloak ID nulo o vacío");
            return false;
        }
        
        // Validación de formato UUID (Keycloak IDs son UUIDs)
        if (!isValidUUID(keycloakId)) {
            log.warn("Keycloak ID con formato inválido (no es UUID): {}", keycloakId);
            return false;
        }
        
        try {
            UsuarioBasicoResponse usuario = usuarioClient.obtenerUsuarioPorKeycloakId(keycloakId);
            boolean existe = usuario != null && usuario.getKeycloakId() != null;
            
            log.debug("Usuario {} - Existe: {}", keycloakId, existe);
            return existe;
            
        } catch (FeignException.NotFound e) {
            log.warn("Usuario no encontrado en microservicio de usuarios. Keycloak ID: {}", keycloakId);
            return false;
            
        } catch (FeignException e) {
            // Error de comunicación con el microservicio - FALLAR RÁPIDO
            log.error("Error al comunicarse con microservicio de usuarios. Status: {} - Mensaje: {}", 
                     e.status(), e.getMessage());
            
            // Lanzar excepción en lugar de permitir la operación
            throw new BusinessLogicException(
                "No se pudo validar el usuario. El servicio de usuarios no está disponible en este momento. " +
                "Por favor, intente nuevamente más tarde."
            );
        }
    }
    
    @Override
    public boolean existeYEstaActivo(String keycloakId) {
        log.debug("Validando que usuario {} exista y esté activo", keycloakId);
        
        // Validación básica de formato
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.error("Keycloak ID nulo o vacío");
            return false;
        }
        
        // Validación de formato UUID
        if (!isValidUUID(keycloakId)) {
            log.warn("Keycloak ID con formato inválido (no es UUID): {}", keycloakId);
            return false;
        }
        
        try {
            UsuarioBasicoResponse usuario = usuarioClient.obtenerUsuarioPorKeycloakId(keycloakId);
            
            // Un usuario está activo si existe y el campo 'enabled' de Keycloak es true
            boolean existeYActivo = usuario != null && 
                                   usuario.getKeycloakId() != null && 
                                   Boolean.TRUE.equals(usuario.getEnabled());
            
            log.debug("Usuario {} - Existe y activo: {}", keycloakId, existeYActivo);
            return existeYActivo;
            
        } catch (FeignException.NotFound e) {
            log.warn("Usuario no encontrado: {}", keycloakId);
            return false;
            
        } catch (FeignException e) {
            log.error("Error al validar usuario activo. Status: {} - Mensaje: {}", 
                     e.status(), e.getMessage());
            
            // Lanzar excepción en lugar de asumir que está activo
            throw new BusinessLogicException(
                "No se pudo validar el estado del usuario. El servicio de usuarios no está disponible en este momento. " +
                "Por favor, intente nuevamente más tarde."
            );
        }
    }
    
    @Override
    public boolean tieneRolApp(String keycloakId, String rolApp) {
        log.debug("Validando que usuario {} tenga rol de aplicación: {}", keycloakId, rolApp);
        
        // Validación básica de formato
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.error("Keycloak ID nulo o vacío");
            return false;
        }
        
        // Validación de formato UUID
        if (!isValidUUID(keycloakId)) {
            log.warn("Keycloak ID con formato inválido (no es UUID): {}", keycloakId);
            return false;
        }
        
        try {
            UsuarioBasicoResponse usuario = usuarioClient.obtenerUsuarioPorKeycloakId(keycloakId);
            
            // Verificar si el rol de la aplicación coincide (case-insensitive)
            boolean tieneRol = usuario != null && 
                              usuario.getRolApp() != null &&
                              usuario.getRolApp().equalsIgnoreCase(rolApp);
            
            log.debug("Usuario {} - Tiene rol {}: {}", keycloakId, rolApp, tieneRol);
            return tieneRol;
            
        } catch (FeignException.NotFound e) {
            log.warn("Usuario no encontrado al validar rol: {}", keycloakId);
            return false;
            
        } catch (FeignException e) {
            log.error("Error al validar rol del usuario. Status: {} - Mensaje: {}", 
                     e.status(), e.getMessage());
            
            // Lanzar excepción en lugar de asumir que tiene el rol
            throw new BusinessLogicException(
                "No se pudo validar el rol del usuario. El servicio de usuarios no está disponible en este momento. " +
                "Por favor, intente nuevamente más tarde."
            );
        }
    }
    
    @Override
    public UsuarioBasicoResponse obtenerUsuario(String keycloakId) {
        log.debug("Obteniendo información de usuario con Keycloak ID: {}", keycloakId);
        
        try {
            UsuarioBasicoResponse usuario = usuarioClient.obtenerUsuarioPorKeycloakId(keycloakId);
            
            log.debug("Usuario {} obtenido exitosamente: {} {}", 
                     keycloakId, usuario.getFirstName(), usuario.getLastName());
            
            return usuario;
            
        } catch (FeignException.NotFound e) {
            log.error("Usuario no encontrado con Keycloak ID: {}", keycloakId);
            throw new ResourceNotFoundException("Usuario no encontrado con Keycloak ID: " + keycloakId);
            
        } catch (FeignException e) {
            log.error("Error al obtener usuario. Status: {} - Mensaje: {}", 
                     e.status(), e.getMessage());
            throw new ResourceNotFoundException(
                    "No se pudo obtener información del usuario. Servicio no disponible."
            );
        }
    }
    
    /**
     * Valida si un string tiene formato de UUID válido.
     * Los Keycloak IDs siempre son UUIDs en el formato: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     * 
     * @param uuid String a validar
     * @return true si tiene formato UUID válido
     */
    private boolean isValidUUID(String uuid) {
        if (uuid == null) {
            return false;
        }
        
        // Regex para validar formato UUID
        String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        
        return uuid.matches(uuidRegex);
    }
}
