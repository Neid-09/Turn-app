package com.turnapp.microservice.turnos_microservice.integration.usuario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Cliente Feign para comunicación con el microservicio de usuarios.
 * Permite validar la existencia de usuarios antes de asignar turnos.
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
    
    /**
     * Obtiene la lista completa de todos los usuarios registrados.
     * Útil para listar usuarios disponibles aplicando lógica híbrida.
     * 
     * Endpoint: GET /usuarios/todos
     * 
     * @return Lista de todos los usuarios en el sistema
     */
    @GetMapping("/todos")
    List<UsuarioBasicoResponse> obtenerTodosLosUsuarios();
}
