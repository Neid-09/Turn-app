package com.turnapp.microservice.horarios_microservice.integration.usuario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicación con el microservicio de usuarios.
 * Permite obtener información de usuarios para enriquecer las respuestas
 * de detalles de horarios con nombres de empleados.
 * 
 * IMPORTANTE: Se usa el keycloakId como identificador único del usuario
 * ya que es el mismo ID usado en Keycloak y en la BD del microservicio de usuarios.
 * 
 * @author TurnApp Team
 */
@FeignClient(
    name = "usuarios-microservices",  // Nombre del servicio en Eureka (con 's' al final)
    path = "/usuarios"                // Path base del API de usuarios
)
public interface UsuarioClient {
    
    /**
     * Obtiene información completa de un usuario por su Keycloak ID.
     * Este endpoint ya existe en el microservicio de usuarios.
     * 
     * Endpoint: GET /usuarios/keycloak/{keycloakId}
     * 
     * @param keycloakId ID del usuario en Keycloak (UUID)
     * @return DTO con información completa del usuario
     * @throws feign.FeignException.NotFound si el usuario no existe (404)
     */
    @GetMapping("/keycloak/{keycloakId}")
    UsuarioBasicoResponse obtenerUsuarioPorKeycloakId(@PathVariable("keycloakId") String keycloakId);
}
