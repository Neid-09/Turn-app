package com.turnapp.microservice.turnos_microservice.reemplazoTurno.controller;

import com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto.ReemplazoRequest;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto.ReemplazoResponse;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.service.IReemplazoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de reemplazos de turnos.
 * Maneja el flujo de solicitud, aprobación y rechazo de reemplazos.
 * 
 * Endpoints disponibles:
 * - POST   /api/reemplazos                    - Solicitar nuevo reemplazo
 * - GET    /api/reemplazos/{id}               - Obtener reemplazo por ID
 * - GET    /api/reemplazos/pendientes         - Listar reemplazos pendientes
 * - GET    /api/reemplazos/reemplazante/{keycloakId} - Listar por reemplazante
 * - PATCH  /api/reemplazos/{id}/aprobar       - Aprobar reemplazo
 * - PATCH  /api/reemplazos/{id}/rechazar      - Rechazar reemplazo
 * 
 * @author TurnApp Team
 */
@Slf4j
@RestController
@RequestMapping("/api/reemplazos")
@RequiredArgsConstructor
public class ReemplazoController {
    
    private final IReemplazoService reemplazoService;
    
    /**
     * Solicitar un nuevo reemplazo de turno.
     * 
     * @param request Datos del reemplazo (asignacionId, reemplazanteId, fecha)
     * @return ReemplazoResponse con el reemplazo creado y código 201
     */
    @PostMapping
    public ResponseEntity<ReemplazoResponse> solicitarReemplazo(@Valid @RequestBody ReemplazoRequest request) {
        log.info("POST /api/reemplazos - Solicitando reemplazo para asignación {}", 
                 request.getAsignacionOriginalId());
        
        ReemplazoResponse reemplazoCreado = reemplazoService.solicitarReemplazo(request);
        
        log.info("Reemplazo solicitado exitosamente con ID: {}", reemplazoCreado.getId());
        return new ResponseEntity<>(reemplazoCreado, HttpStatus.CREATED);
    }
    
    /**
     * Obtener un reemplazo por su ID.
     * 
     * @param id ID del reemplazo
     * @return ReemplazoResponse con los datos del reemplazo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReemplazoResponse> obtenerReemplazoPorId(@PathVariable Long id) {
        log.info("GET /api/reemplazos/{} - Obteniendo reemplazo", id);
        
        ReemplazoResponse reemplazo = reemplazoService.obtenerReemplazoPorId(id);
        
        log.info("Reemplazo {} encontrado con estado {}", id, reemplazo.getEstado());
        return ResponseEntity.ok(reemplazo);
    }
    
    /**
     * Obtener todas las solicitudes de reemplazo pendientes.
     * Útil para administradores que necesitan aprobar/rechazar.
     * 
     * @return Lista de reemplazos pendientes de aprobación
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<ReemplazoResponse>> listarReemplazosPendientes() {
        log.info("GET /api/reemplazos/pendientes - Obteniendo reemplazos pendientes");
        
        List<ReemplazoResponse> reemplazos = reemplazoService.obtenerReemplazosPendientes();
        
        log.info("Se encontraron {} reemplazos pendientes", reemplazos.size());
        return ResponseEntity.ok(reemplazos);
    }
    
    /**
     * Obtener todos los reemplazos donde un usuario es el reemplazante.
     * 
     * @param keycloakId Keycloak ID del reemplazante
     * @return Lista de reemplazos del usuario
     */
    @GetMapping("/reemplazante/{keycloakId}")
    public ResponseEntity<List<ReemplazoResponse>> obtenerReemplazosPorReemplazante(
            @PathVariable String keycloakId) {
        
        log.info("GET /api/reemplazos/reemplazante/{} - Obteniendo reemplazos", keycloakId);
        
        List<ReemplazoResponse> reemplazos = 
                reemplazoService.obtenerReemplazosPorReemplazante(keycloakId);
        
        log.info("Se encontraron {} reemplazos para el reemplazante {}", 
                 reemplazos.size(), keycloakId);
        return ResponseEntity.ok(reemplazos);
    }
    
    /**
     * Aprobar una solicitud de reemplazo.
     * Cambia el estado a APROBADO y actualiza la asignación.
     * 
     * @param id ID del reemplazo
     * @param aprobadorId ID del administrador que aprueba (opcional)
     * @return ReemplazoResponse con el reemplazo aprobado
     */
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<ReemplazoResponse> aprobarReemplazo(
            @PathVariable Long id,
            @RequestParam(required = false) Long aprobadorId) {
        
        log.info("PATCH /api/reemplazos/{}/aprobar - Aprobando reemplazo. Aprobador: {}", 
                 id, aprobadorId);
        
        ReemplazoResponse reemplazoAprobado = reemplazoService.aprobarReemplazo(id, aprobadorId);
        
        log.info("Reemplazo {} aprobado exitosamente", id);
        return ResponseEntity.ok(reemplazoAprobado);
    }
    
    /**
     * Rechazar una solicitud de reemplazo.
     * 
     * @param id ID del reemplazo
     * @param aprobadorId ID del administrador que rechaza (opcional)
     * @param motivo Motivo del rechazo (opcional)
     * @return ReemplazoResponse con el reemplazo rechazado
     */
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<ReemplazoResponse> rechazarReemplazo(
            @PathVariable Long id,
            @RequestParam(required = false) Long aprobadorId,
            @RequestParam(required = false) String motivo) {
        
        log.info("PATCH /api/reemplazos/{}/rechazar - Rechazando reemplazo. Motivo: {}", id, motivo);
        
        ReemplazoResponse reemplazoRechazado = 
                reemplazoService.rechazarReemplazo(id, aprobadorId, motivo);
        
        log.info("Reemplazo {} rechazado exitosamente", id);
        return ResponseEntity.ok(reemplazoRechazado);
    }
}
