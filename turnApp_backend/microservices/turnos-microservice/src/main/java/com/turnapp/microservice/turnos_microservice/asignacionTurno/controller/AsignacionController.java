package com.turnapp.microservice.turnos_microservice.asignacionTurno.controller;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.dto.AsignacionRequest;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.dto.AsignacionResponse;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.service.IAsignacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de asignaciones de turnos.
 * Este es el controlador principal del sistema, maneja la asignación de turnos a usuarios
 * con validación de solapamientos y disponibilidad.
 * 
 * Endpoints disponibles:
 * - POST   /api/asignaciones              - Crear nueva asignación
 * - GET    /api/asignaciones/{id}         - Obtener asignación por ID
 * - GET    /api/asignaciones/usuario/{keycloakId} - Listar asignaciones por usuario
 * - GET    /api/asignaciones/fecha        - Listar asignaciones por fecha
 * - PATCH  /api/asignaciones/{id}/cancelar - Cancelar asignación
 * - PATCH  /api/asignaciones/{id}/completar - Completar asignación
 * 
 * @author TurnApp Team
 */
@Slf4j
@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {
    
    private final IAsignacionService asignacionService;
    
    /**
     * Crear una nueva asignación de turno.
     * Valida solapamientos y disponibilidad automáticamente.
     * 
     * @param request Datos de la asignación (usuarioId, turnoId, fecha)
     * @return AsignacionResponse con la asignación creada y código 201
     */
    @PostMapping
    public ResponseEntity<AsignacionResponse> crearAsignacion(@Valid @RequestBody AsignacionRequest request) {
        log.info("POST /api/asignaciones - Creando asignación para usuario {} en fecha {}", 
                 request.getUsuarioId(), request.getFecha());
        
        AsignacionResponse asignacionCreada = asignacionService.crearAsignacion(request);
        
        log.info("Asignación creada exitosamente con ID: {}", asignacionCreada.getId());
        return new ResponseEntity<>(asignacionCreada, HttpStatus.CREATED);
    }
    
    /**
     * Obtener una asignación por su ID.
     * 
     * @param id ID de la asignación
     * @return AsignacionResponse con los datos de la asignación
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionResponse> obtenerAsignacionPorId(@PathVariable Long id) {
        log.info("GET /api/asignaciones/{} - Obteniendo asignación", id);
        
        AsignacionResponse asignacion = asignacionService.obtenerAsignacionPorId(id);
        
        log.info("Asignación {} encontrada para usuario {}", id, asignacion.getUsuarioId());
        return ResponseEntity.ok(asignacion);
    }
    
    /**
     * Obtener todas las asignaciones de un usuario.
     * 
     * @param keycloakId Keycloak ID del usuario
     * @return Lista de asignaciones del usuario
     */
    @GetMapping("/usuario/{keycloakId}")
    public ResponseEntity<List<AsignacionResponse>> obtenerAsignacionesPorUsuario(
            @PathVariable String keycloakId) {
        
        log.info("GET /api/asignaciones/usuario/{} - Obteniendo asignaciones del usuario", keycloakId);
        
        List<AsignacionResponse> asignaciones = asignacionService.obtenerAsignacionesPorUsuario(keycloakId);
        
        log.info("Se encontraron {} asignaciones para el usuario {}", asignaciones.size(), keycloakId);
        return ResponseEntity.ok(asignaciones);
    }
    
    /**
     * Obtener todas las asignaciones en una fecha específica.
     * 
     * @param fecha Fecha en formato yyyy-MM-dd
     * @return Lista de asignaciones en la fecha especificada
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<AsignacionResponse>> obtenerAsignacionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        log.info("GET /api/asignaciones/fecha?fecha={} - Obteniendo asignaciones", fecha);
        
        List<AsignacionResponse> asignaciones = asignacionService.obtenerAsignacionesPorFecha(fecha);
        
        log.info("Se encontraron {} asignaciones para la fecha {}", asignaciones.size(), fecha);
        return ResponseEntity.ok(asignaciones);
    }
    
    /**
     * Obtener todas las asignaciones en un rango de fechas (período).
     * Útil para consolidar horarios en el microservicio de Horarios.
     * 
     * @param fechaInicio Fecha de inicio del período (yyyy-MM-dd)
     * @param fechaFin Fecha de fin del período (yyyy-MM-dd)
     * @return Lista de asignaciones en el rango especificado
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<AsignacionResponse>> obtenerAsignacionesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        log.info("GET /api/asignaciones/periodo?fechaInicio={}&fechaFin={} - Obteniendo asignaciones", 
                 fechaInicio, fechaFin);
        
        List<AsignacionResponse> asignaciones = 
                asignacionService.obtenerAsignacionesPorPeriodo(fechaInicio, fechaFin);
        
        log.info("Se encontraron {} asignaciones entre {} y {}", 
                 asignaciones.size(), fechaInicio, fechaFin);
        return ResponseEntity.ok(asignaciones);
    }
    
    /**
     * Cancelar una asignación de turno.
     * No elimina el registro, solo cambia el estado a CANCELADO.
     * 
     * @param id ID de la asignación a cancelar
     * @param motivo Motivo de la cancelación (opcional)
     * @return ResponseEntity sin contenido (204)
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarAsignacion(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        
        log.info("PATCH /api/asignaciones/{}/cancelar - Cancelando asignación. Motivo: {}", id, motivo);
        
        asignacionService.cancelarAsignacion(id, motivo);
        
        log.info("Asignación {} cancelada exitosamente", id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Marcar una asignación como completada.
     * 
     * @param id ID de la asignación
     * @return ResponseEntity sin contenido (204)
     */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Void> completarAsignacion(@PathVariable Long id) {
        log.info("PATCH /api/asignaciones/{}/completar - Completando asignación", id);
        
        asignacionService.completarAsignacion(id);
        
        log.info("Asignación {} completada exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
