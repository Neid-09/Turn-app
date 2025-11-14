package com.turnapp.microservice.horarios_microservice.integration.turnos.client;

import com.turnapp.microservice.horarios_microservice.integration.turnos.dto.AsignacionRequest;
import com.turnapp.microservice.horarios_microservice.integration.turnos.dto.AsignacionResponse;
import com.turnapp.microservice.horarios_microservice.integration.turnos.dto.DisponibilidadResponse;
import com.turnapp.microservice.horarios_microservice.integration.turnos.dto.TurnoResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Cliente Feign para comunicación con el microservicio de turnos.
 * 
 * Proporciona métodos para:
 * - Obtener turnos activos
 * - Consultar asignaciones por período
 * - Crear asignaciones (publicación de horarios)
 * - Obtener disponibilidades de usuarios
 * 
 * El token JWT se propaga automáticamente mediante el interceptor configurado en FeignConfig.
 * 
 * NOTA PARA ESCALABILIDAD:
 * La creación de asignaciones actualmente se hace de forma síncrona (una por una).
 * Si el volumen de publicaciones crece significativamente, se recomienda migrar a un
 * patrón asíncrono usando eventos (ej: Apache Kafka, RabbitMQ) para mejorar el rendimiento
 * y la tolerancia a fallos.
 * 
 * @author TurnApp Team
 */
@FeignClient(
    name = "turnos-microservice",
    path = "/api"
)
public interface TurnosClient {
    
    /**
     * Obtiene todas las plantillas de turnos activos disponibles para asignar.
     * 
     * Endpoint: GET /api/turnos/activos
     * 
     * @return Lista de turnos activos
     */
    @GetMapping("/turnos/activos")
    List<TurnoResponse> obtenerTurnosActivos();
    
    /**
     * Obtiene las asignaciones de turno en un rango de fechas.
     * 
     * Este es el endpoint CRÍTICO para generar vistas consolidadas de horarios
     * y reportes de disponibilidad.
     * 
     * Endpoint: GET /api/asignaciones/periodo?fechaInicio={inicio}&fechaFin={fin}
     * 
     * @param fechaInicio Fecha de inicio del período (formato: YYYY-MM-DD)
     * @param fechaFin Fecha de fin del período (formato: YYYY-MM-DD)
     * @return Lista de asignaciones en el período especificado
     */
    @GetMapping("/asignaciones/periodo")
    List<AsignacionResponse> obtenerAsignacionesPorPeriodo(
        @RequestParam("fechaInicio") 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        LocalDate fechaInicio,
        
        @RequestParam("fechaFin") 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        LocalDate fechaFin
    );
    
    /**
     * Crea una nueva asignación de turno (delegación al microservicio de turnos).
     * 
     * El microservicio de turnos se encarga de:
     * - Validar que el usuario existe
     * - Validar que el turno existe y está activo
     * - Validar solapamientos de horarios
     * - Validar preferencias de disponibilidad (sistema híbrido)
     * 
     * Endpoint: POST /api/asignaciones
     * 
     * NOTA DE ESCALABILIDAD:
     * Actualmente síncrono. Para publicaciones masivas (>100 asignaciones),
     * considerar patrón asíncrono con eventos para mejor rendimiento.
     * 
     * @param request Datos de la asignación a crear
     * @return Asignación creada con ID generado
     */
    @PostMapping("/asignaciones")
    AsignacionResponse crearAsignacion(@RequestBody AsignacionRequest request);
    
    /**
     * Obtiene las preferencias de disponibilidad de un usuario específico.
     * 
     * Útil para validar preferencias antes de generar horarios automáticos.
     * 
     * Endpoint: GET /api/disponibilidades/usuario/{keycloakId}
     * 
     * @param keycloakId UUID del usuario en Keycloak
     * @return Lista de disponibilidades del usuario
     */
    @GetMapping("/disponibilidades/usuario/{keycloakId}")
    List<DisponibilidadResponse> obtenerDisponibilidadesPorUsuario(
        @PathVariable("keycloakId") String keycloakId
    );
}
