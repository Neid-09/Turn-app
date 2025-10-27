package com.turnapp.microservice.turnos_microservice.turno.controller;

import com.turnapp.microservice.turnos_microservice.turno.dto.TurnoRequest;
import com.turnapp.microservice.turnos_microservice.turno.dto.TurnoResponse;
import com.turnapp.microservice.turnos_microservice.turno.service.ITurnoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de turnos (plantillas de turnos).
 * 
 * Endpoints disponibles:
 * - POST   /api/turnos              - Crear nuevo turno
 * - GET    /api/turnos              - Listar todos los turnos
 * - GET    /api/turnos/{id}         - Obtener turno por ID
 * - GET    /api/turnos/estado/{estado} - Listar turnos por estado
 * - PUT    /api/turnos/{id}         - Actualizar turno
 * - DELETE /api/turnos/{id}         - Eliminar turno (soft delete)
 * 
 * @author TurnApp Team
 */
@Slf4j
@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {
    
    private final ITurnoService turnoService;
    
    /**
     * Crear un nuevo turno (plantilla).
     * 
     * @param request Datos del turno a crear
     * @return TurnoResponse con el turno creado y código 201
     */
    @PostMapping
    public ResponseEntity<TurnoResponse> crearTurno(@Valid @RequestBody TurnoRequest request) {
        log.info("POST /api/turnos - Creando nuevo turno: {}", request.getNombre());
        
        TurnoResponse turnoCreado = turnoService.crearTurno(request);
        
        log.info("Turno creado exitosamente con ID: {}", turnoCreado.getId());
        return new ResponseEntity<>(turnoCreado, HttpStatus.CREATED);
    }
    
    /**
     * Obtener todos los turnos.
     * 
     * @return Lista de todos los turnos
     */
    @GetMapping
    public ResponseEntity<List<TurnoResponse>> listarTurnos() {
        log.info("GET /api/turnos - Obteniendo todos los turnos");
        
        List<TurnoResponse> turnos = turnoService.obtenerTodosTurnos();
        
        log.info("Se encontraron {} turnos", turnos.size());
        return ResponseEntity.ok(turnos);
    }
    
    /**
     * Obtener un turno por su ID.
     * 
     * @param id ID del turno
     * @return TurnoResponse con los datos del turno
     */
    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> obtenerTurnoPorId(@PathVariable Long id) {
        log.info("GET /api/turnos/{} - Obteniendo turno", id);
        
        TurnoResponse turno = turnoService.obtenerTurnoPorId(id);
        
        log.info("Turno {} encontrado: {}", id, turno.getNombre());
        return ResponseEntity.ok(turno);
    }
    
    /**
     * Obtener turnos activos.
     * 
     * @return Lista de turnos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<TurnoResponse>> listarTurnosActivos() {
        log.info("GET /api/turnos/activos - Obteniendo turnos activos");
        
        List<TurnoResponse> turnos = turnoService.obtenerTurnosActivos();
        
        log.info("Se encontraron {} turnos activos", turnos.size());
        return ResponseEntity.ok(turnos);
    }
    
    /**
     * Actualizar un turno existente.
     * 
     * @param id ID del turno a actualizar
     * @param request Nuevos datos del turno
     * @return TurnoResponse con el turno actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponse> actualizarTurno(
            @PathVariable Long id,
            @Valid @RequestBody TurnoRequest request) {
        
        log.info("PUT /api/turnos/{} - Actualizando turno", id);
        
        TurnoResponse turnoActualizado = turnoService.actualizarTurno(id, request);
        
        log.info("Turno {} actualizado exitosamente", id);
        return ResponseEntity.ok(turnoActualizado);
    }
    
    /**
     * Eliminar un turno (soft delete).
     * Cambia el estado a INACTIVO en lugar de eliminar físicamente.
     * 
     * @param id ID del turno a eliminar
     * @return ResponseEntity sin contenido (204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        log.info("DELETE /api/turnos/{} - Eliminando turno", id);
        
        turnoService.eliminarTurno(id);
        
        log.info("Turno {} eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
