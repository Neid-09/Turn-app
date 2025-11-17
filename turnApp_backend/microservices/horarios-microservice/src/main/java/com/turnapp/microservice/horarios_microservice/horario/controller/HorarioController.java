package com.turnapp.microservice.horarios_microservice.horario.controller;

import com.turnapp.microservice.horarios_microservice.horario.dto.*;
import com.turnapp.microservice.horarios_microservice.horario.service.IHorarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de horarios.
 * 
 * Expone endpoints para:
 * - CRUD de horarios
 * - Gestión de detalles
 * - Publicación de horarios
 * - Consultas consolidadas
 * 
 * SEGURIDAD:
 * - Todos los endpoints requieren autenticación JWT
 * - EMPLEADO: Solo lectura (GET)
 * - ADMIN: Acceso completo (GET, POST, PUT, PATCH, DELETE)
 * 
 * @author TurnApp Team
 */
@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@Slf4j
public class HorarioController {
    
    private final IHorarioService horarioService;
    
    // ================== ENDPOINTS DE HORARIOS ==================
    
    /**
     * Crea un nuevo horario en estado BORRADOR.
     * 
     * POST /api/horarios
     * 
     * @param request Datos del horario
     * @param jwt Token JWT del usuario autenticado
     * @return 201 Created con el horario creado
     */
    @PostMapping
    public ResponseEntity<HorarioResponse> crearHorario(
            @Valid @RequestBody HorarioRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String usuarioId = jwt.getSubject(); // UUID de Keycloak
        log.info("POST /api/horarios - Usuario: {}", usuarioId);
        
        HorarioResponse response = horarioService.crearHorario(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Lista todos los horarios.
     * 
     * GET /api/horarios
     * 
     * @return 200 OK con lista de horarios
     */
    @GetMapping
    public ResponseEntity<List<HorarioResponse>> listarHorarios() {
        log.info("GET /api/horarios");
        
        List<HorarioResponse> horarios = horarioService.listarHorarios();
        return ResponseEntity.ok(horarios);
    }
    
    /**
     * Obtiene un horario por ID.
     * 
     * GET /api/horarios/{id}?incluirDetalles=true|false
     * 
     * @param id ID del horario
     * @param incluirDetalles Si debe incluir lista de detalles (default: false)
     * @return 200 OK con el horario
     */
    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponse> obtenerHorarioPorId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean incluirDetalles) {
        
        log.info("GET /api/horarios/{} - incluirDetalles: {}", id, incluirDetalles);
        
        HorarioResponse response = horarioService.obtenerHorarioPorId(id, incluirDetalles);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Actualiza un horario existente.
     * Solo horarios en estado BORRADOR pueden ser actualizados.
     * 
     * PUT /api/horarios/{id}
     * 
     * @param id ID del horario
     * @param request Nuevos datos
     * @return 200 OK con el horario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponse> actualizarHorario(
            @PathVariable Long id,
            @Valid @RequestBody HorarioRequest request) {
        
        log.info("PUT /api/horarios/{}", id);
        
        HorarioResponse response = horarioService.actualizarHorario(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Elimina un horario.
     * Solo horarios en estado BORRADOR pueden ser eliminados.
     * 
     * DELETE /api/horarios/{id}
     * 
     * @param id ID del horario
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id) {
        log.info("DELETE /api/horarios/{}", id);
        
        horarioService.eliminarHorario(id);
        return ResponseEntity.noContent().build();
    }
    
    // ================== ENDPOINTS DE DETALLES ==================
    
    /**
     * Agrega un detalle (asignación planificada) a un horario.
     * 
     * POST /api/horarios/{id}/detalles
     * 
     * @param id ID del horario
     * @param request Datos del detalle
     * @return 201 Created con el detalle creado
     */
    @PostMapping("/{id}/detalles")
    public ResponseEntity<HorarioDetalleResponse> agregarDetalle(
            @PathVariable Long id,
            @Valid @RequestBody HorarioDetalleRequest request) {
        
        log.info("POST /api/horarios/{}/detalles", id);
        
        HorarioDetalleResponse response = horarioService.agregarDetalle(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Agrega múltiples detalles de forma masiva.
     * 
     * POST /api/horarios/{id}/detalles/lote
     * 
     * @param id ID del horario
     * @param requests Lista de detalles
     * @return 201 Created con la lista de detalles creados
     */
    @PostMapping("/{id}/detalles/lote")
    public ResponseEntity<List<HorarioDetalleResponse>> agregarDetallesEnLote(
            @PathVariable Long id,
            @Valid @RequestBody List<HorarioDetalleRequest> requests) {
        
        log.info("POST /api/horarios/{}/detalles/lote - {} detalles", id, requests.size());
        
        List<HorarioDetalleResponse> responses = horarioService.agregarDetallesEnLote(id, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
    
    /**
     * Elimina un detalle del horario.
     * 
     * DELETE /api/horarios/{horarioId}/detalles/{detalleId}
     * 
     * @param horarioId ID del horario
     * @param detalleId ID del detalle
     * @return 204 No Content
     */
    @DeleteMapping("/{horarioId}/detalles/{detalleId}")
    public ResponseEntity<Void> eliminarDetalle(
            @PathVariable Long horarioId,
            @PathVariable Long detalleId) {
        
        log.info("DELETE /api/horarios/{}/detalles/{}", horarioId, detalleId);
        
        horarioService.eliminarDetalle(horarioId, detalleId);
        return ResponseEntity.noContent().build();
    }
    
    // ================== PUBLICACIÓN ==================
    
    /**
     * Publica un horario, creando asignaciones en turnos-microservice.
     * 
     * PROCESO:
     * - Valida que el horario esté en BORRADOR y tenga detalles
     * - Crea asignaciones síncronas en turnos-microservice (una por una)
     * - Retorna reporte detallado de éxitos y fallos
     * - Cambia el estado del horario a PUBLICADO
     * 
     * NOTA DE ESCALABILIDAD:
     * Para volúmenes altos (>100 asignaciones), considerar migración a eventos asíncronos.
     * 
     * POST /api/horarios/{id}/publicar
     * 
     * @param id ID del horario
     * @return 200 OK con reporte de publicación
     */
    @PostMapping("/{id}/publicar")
    public ResponseEntity<ReportePublicacionResponse> publicarHorario(@PathVariable Long id) {
        log.info("POST /api/horarios/{}/publicar - Iniciando publicación", id);
        
        ReportePublicacionResponse reporte = horarioService.publicarHorario(id);
        
        if (reporte.esExitosaCompleta()) {
            log.info("✅ Publicación completada exitosamente para horario ID: {}", id);
            return ResponseEntity.ok(reporte);
        } else if (reporte.esExitosaParcial()) {
            log.warn("⚠️ Publicación parcialmente exitosa para horario ID: {}. " +
                     "Exitosos: {}, Fallidos: {}", 
                     id, reporte.getTotalExitosos(), reporte.getTotalFallidos());
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(reporte);
        } else {
            log.error("❌ Publicación fallida para horario ID: {}. Todos los detalles fallaron.", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(reporte);
        }
    }
    
    // ================== CONSULTAS CONSOLIDADAS ==================
    
    /**
     * Obtiene la vista consolidada de un horario.
     * 
     * Combina:
     * - Información del horario local
     * - Asignaciones reales del microservicio de turnos
     * - Estadísticas calculadas
     * 
     * GET /api/horarios/{id}/consolidado
     * 
     * @param id ID del horario
     * @return 200 OK con vista consolidada
     */
    @GetMapping("/{id}/consolidado")
    public ResponseEntity<HorarioConsolidadoResponse> obtenerHorarioConsolidado(@PathVariable Long id) {
        log.info("GET /api/horarios/{}/consolidado", id);
        
        HorarioConsolidadoResponse response = horarioService.obtenerHorarioConsolidado(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca horarios que cubren una fecha específica.
     * 
     * GET /api/horarios/fecha?fecha=2025-12-01
     * 
     * @param fecha Fecha a buscar (formato: YYYY-MM-DD)
     * @return 200 OK con lista de horarios que incluyen la fecha
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<HorarioResponse>> buscarHorariosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        log.info("GET /api/horarios/fecha?fecha={}", fecha);
        
        List<HorarioResponse> horarios = horarioService.buscarHorariosPorFecha(fecha);
        return ResponseEntity.ok(horarios);
    }
}
