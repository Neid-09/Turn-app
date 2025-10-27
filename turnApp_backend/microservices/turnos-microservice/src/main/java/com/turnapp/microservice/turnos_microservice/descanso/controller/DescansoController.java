package com.turnapp.microservice.turnos_microservice.descanso.controller;

import com.turnapp.microservice.turnos_microservice.descanso.dto.DescansoRequest;
import com.turnapp.microservice.turnos_microservice.descanso.dto.DescansoResponse;
import com.turnapp.microservice.turnos_microservice.descanso.service.IDescansoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de descansos durante turnos.
 * Permite registrar y consultar pausas/breaks dentro de las asignaciones.
 * 
 * Endpoints disponibles:
 * - POST   /api/descansos                        - Registrar nuevo descanso
 * - GET    /api/descansos/{id}                   - Obtener descanso por ID
 * - GET    /api/descansos/asignacion/{asignacionId} - Listar descansos por asignación
 * - DELETE /api/descansos/{id}                   - Eliminar descanso
 * 
 * @author TurnApp Team
 */
@Slf4j
@RestController
@RequestMapping("/api/descansos")
@RequiredArgsConstructor
public class DescansoController {
    
    private final IDescansoService descansoService;
    
    /**
     * Registrar un nuevo descanso durante un turno.
     * 
     * @param request Datos del descanso (asignacionId, horaInicio, horaFin, tipo)
     * @return DescansoResponse con el descanso registrado y código 201
     */
    @PostMapping
    public ResponseEntity<DescansoResponse> registrarDescanso(@Valid @RequestBody DescansoRequest request) {
        log.info("POST /api/descansos - Registrando descanso para asignación {}", 
                 request.getAsignacionId());
        
        DescansoResponse descansoCreado = descansoService.registrarDescanso(request);
        
        log.info("Descanso registrado exitosamente con ID: {}", descansoCreado.getId());
        return new ResponseEntity<>(descansoCreado, HttpStatus.CREATED);
    }
    
    /**
     * Obtener un descanso por su ID.
     * 
     * @param id ID del descanso
     * @return DescansoResponse con los datos del descanso
     */
    @GetMapping("/{id}")
    public ResponseEntity<DescansoResponse> obtenerDescansoPorId(@PathVariable Long id) {
        log.info("GET /api/descansos/{} - Obteniendo descanso", id);
        
        DescansoResponse descanso = descansoService.obtenerDescansoPorId(id);
        
        log.info("Descanso {} encontrado - Tipo: {}", id, descanso.getTipo());
        return ResponseEntity.ok(descanso);
    }
    
    /**
     * Obtener todos los descansos de una asignación específica.
     * 
     * @param asignacionId ID de la asignación
     * @return Lista de descansos de la asignación
     */
    @GetMapping("/asignacion/{asignacionId}")
    public ResponseEntity<List<DescansoResponse>> obtenerDescansosPorAsignacion(
            @PathVariable Long asignacionId) {
        
        log.info("GET /api/descansos/asignacion/{} - Obteniendo descansos", asignacionId);
        
        List<DescansoResponse> descansos = 
                descansoService.obtenerDescansosPorAsignacion(asignacionId);
        
        log.info("Se encontraron {} descansos para la asignación {}", 
                 descansos.size(), asignacionId);
        return ResponseEntity.ok(descansos);
    }
    
    /**
     * Eliminar un descanso registrado.
     * 
     * @param id ID del descanso a eliminar
     * @return ResponseEntity sin contenido (204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDescanso(@PathVariable Long id) {
        log.info("DELETE /api/descansos/{} - Eliminando descanso", id);
        
        descansoService.eliminarDescanso(id);
        
        log.info("Descanso {} eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
