package com.turnapp.microservice.turnos_microservice.disponibilidad.controller;

import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadRequest;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.UsuarioDisponibleResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.model.DiaSemana;
import com.turnapp.microservice.turnos_microservice.disponibilidad.service.IDisponibilidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador REST para la gestión de disponibilidad preferencial de usuarios.
 * Permite configurar los horarios en los que los usuarios prefieren trabajar.
 * 
 * Endpoints disponibles:
 * - POST   /api/disponibilidades              - Crear nueva disponibilidad
 * - GET    /api/disponibilidades/{id}         - Obtener disponibilidad por ID
 * - GET    /api/disponibilidades/usuario/{keycloakId} - Listar disponibilidades por usuario
 * - GET    /api/disponibilidades/periodo      - Listar disponibilidades por rango de fechas
 * - GET    /api/disponibilidades/usuarios-disponibles - Listar usuarios disponibles (SISTEMA HÍBRIDO)
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
     * Obtener disponibilidades configuradas en un rango de fechas.
     * Útil para calcular empleados disponibles en el microservicio de Horarios.
     * 
     * @param fechaInicio Fecha de inicio del período (yyyy-MM-dd)
     * @param fechaFin Fecha de fin del período (yyyy-MM-dd)
     * @return Lista de disponibilidades en el rango especificado
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<DisponibilidadResponse>> obtenerDisponibilidadesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        log.info("GET /api/disponibilidades/periodo?fechaInicio={}&fechaFin={} - Obteniendo disponibilidades", 
                 fechaInicio, fechaFin);
        
        List<DisponibilidadResponse> disponibilidades = 
                disponibilidadService.obtenerDisponibilidadesPorPeriodo(fechaInicio, fechaFin);
        
        log.info("Se encontraron {} disponibilidades configuradas en el período", 
                 disponibilidades.size());
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
    
    /**
     * Obtener usuarios disponibles para una fecha y horario específico.
     * 
     * Aplica la lógica HÍBRIDA del sistema:
     * - Todos los usuarios están disponibles por defecto
     * - Se consulta el microservicio de usuarios
     * - Se validan preferencias horarias si las hay configuradas
     * - Se excluyen usuarios que ya tienen asignaciones en la fecha especificada
     * 
     * OPTIMIZACIÓN: El día de la semana se calcula automáticamente desde la fecha,
     * eliminando redundancia y posibles inconsistencias.
     * 
     * IMPORTANTE: Todos los parámetros son OBLIGATORIOS
     * 
     * @param fecha Fecha específica para verificar asignaciones (formato yyyy-MM-dd, ejemplo: 2025-11-27)
     * @param horaInicio Hora de inicio del turno (formato HH:mm, ejemplo: 08:00)
     * @param horaFin Hora de fin del turno (formato HH:mm, ejemplo: 16:00)
     * @return Lista de usuarios disponibles con información de preferencias
     * 
     * Ejemplo de petición:
     * GET /api/disponibilidades/usuarios-disponibles?fecha=2025-11-27&horaInicio=08:00&horaFin=16:00
     */
    @GetMapping("/usuarios-disponibles")
    public ResponseEntity<List<UsuarioDisponibleResponse>> obtenerUsuariosDisponibles(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = true) @DateTimeFormat(pattern = "HH:mm") LocalTime horaInicio,
            @RequestParam(required = true) @DateTimeFormat(pattern = "HH:mm") LocalTime horaFin) {
        
        // Calcular el día de la semana desde la fecha y convertir a español
        DiaSemana diaSemana = DiaSemana.fromDayOfWeek(fecha.getDayOfWeek());
        
        log.info("GET /api/disponibilidades/usuarios-disponibles?fecha={}&horaInicio={}&horaFin={} (día calculado: {})", 
                 fecha, horaInicio, horaFin, diaSemana);
        
        List<UsuarioDisponibleResponse> usuariosDisponibles = 
                disponibilidadService.obtenerUsuariosDisponibles(fecha, diaSemana, horaInicio, horaFin);
        
        log.info("Se encontraron {} usuarios disponibles para {} ({}) de {}:{} a {}:{}", 
                 usuariosDisponibles.size(), fecha, diaSemana, 
                 horaInicio.getHour(), horaInicio.getMinute(),
                 horaFin.getHour(), horaFin.getMinute());
        return ResponseEntity.ok(usuariosDisponibles);
    }
}
