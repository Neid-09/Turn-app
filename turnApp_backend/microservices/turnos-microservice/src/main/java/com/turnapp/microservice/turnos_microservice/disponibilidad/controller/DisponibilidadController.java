package com.turnapp.microservice.turnos_microservice.disponibilidad.controller;

import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadRequest;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.service.IDisponibilidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de disponibilidad preferencial de usuarios.
 * Permite configurar los horarios en los que los usuarios prefieren trabajar.
 * 
 * Endpoints disponibles:
 * - POST   /api/disponibilidades              - Crear nueva disponibilidad
 * - GET    /api/disponibilidades/{id}         - Obtener disponibilidad por ID
 * - GET    /api/disponibilidades/usuario/{keycloakId} - Listar disponibilidades por usuario
 * - PUT    /api/disponibilidades/{id}         - Actualizar disponibilidad
 * - PATCH  /api/disponibilidades/{id}/desactivar - Desactivar disponibilidad
 * - DELETE /api/disponibilidades/{id}         - Eliminar disponibilidad
 * 
 * @author TurnApp Team
 */
@Slf4j
@RestController
@RequestMapping("/api/disponibilidades")
@RequiredArgsConstructor
public class DisponibilidadController {
    
    private final IDisponibilidadService disponibilidadService;
    
    /**
     * Crear una nueva regla de disponibilidad preferencial.
     * 
     * @param request Datos de la disponibilidad (usuarioId, diaSemana, horarios)
     * @return DisponibilidadResponse con la disponibilidad creada y código 201
     */
    @PostMapping
    public ResponseEntity<DisponibilidadResponse> crearDisponibilidad(
            @Valid @RequestBody DisponibilidadRequest request) {
        
        log.info("POST /api/disponibilidades - Creando disponibilidad para usuario {} - Día: {}", 
                 request.getUsuarioId(), request.getDiaSemana());
        
        DisponibilidadResponse disponibilidadCreada = disponibilidadService.crearDisponibilidad(request);
        
        log.info("Disponibilidad creada exitosamente con ID: {}", disponibilidadCreada.getId());
        return new ResponseEntity<>(disponibilidadCreada, HttpStatus.CREATED);
    }
    
    /**
     * Obtener una disponibilidad por su ID.
     * 
     * @param id ID de la disponibilidad
     * @return DisponibilidadResponse con los datos de la disponibilidad
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> obtenerDisponibilidadPorId(@PathVariable Long id) {
        log.info("GET /api/disponibilidades/{} - Obteniendo disponibilidad", id);
        
        DisponibilidadResponse disponibilidad = disponibilidadService.obtenerDisponibilidadPorId(id);
        
        log.info("Disponibilidad {} encontrada", id);
        return ResponseEntity.ok(disponibilidad);
    }
    
    /**
     * Obtener todas las disponibilidades de un usuario.
     * 
     * @param keycloakId Keycloak ID del usuario
     * @return Lista de disponibilidades del usuario
     */
    @GetMapping("/usuario/{keycloakId}")
    public ResponseEntity<List<DisponibilidadResponse>> obtenerDisponibilidadesPorUsuario(
            @PathVariable String keycloakId) {
        
        log.info("GET /api/disponibilidades/usuario/{} - Obteniendo disponibilidades", keycloakId);
        
        List<DisponibilidadResponse> disponibilidades = 
                disponibilidadService.obtenerDisponibilidadesPorUsuario(keycloakId);
        
        log.info("Se encontraron {} disponibilidades para el usuario {}", 
                 disponibilidades.size(), keycloakId);
        return ResponseEntity.ok(disponibilidades);
    }
    
    /**
     * Actualizar una disponibilidad existente.
     * 
     * @param id ID de la disponibilidad a actualizar
     * @param request Nuevos datos de la disponibilidad
     * @return DisponibilidadResponse con la disponibilidad actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> actualizarDisponibilidad(
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadRequest request) {
        
        log.info("PUT /api/disponibilidades/{} - Actualizando disponibilidad", id);
        
        DisponibilidadResponse disponibilidadActualizada = 
                disponibilidadService.actualizarDisponibilidad(id, request);
        
        log.info("Disponibilidad {} actualizada exitosamente", id);
        return ResponseEntity.ok(disponibilidadActualizada);
    }
    
    /**
     * Eliminar una disponibilidad permanentemente.
     * 
     * @param id ID de la disponibilidad a eliminar
     * @return ResponseEntity sin contenido (204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDisponibilidad(@PathVariable Long id) {
        log.info("DELETE /api/disponibilidades/{} - Eliminando disponibilidad", id);
        
        disponibilidadService.eliminarDisponibilidad(id);
        
        log.info("Disponibilidad {} eliminada exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
