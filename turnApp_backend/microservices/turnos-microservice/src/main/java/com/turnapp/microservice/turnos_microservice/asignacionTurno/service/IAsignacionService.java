package com.turnapp.microservice.turnos_microservice.asignacionTurno.service;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.dto.AsignacionRequest;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.dto.AsignacionResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de asignaciones de turno.
 * Define las operaciones de negocio para asignar turnos a usuarios,
 * incluyendo la validación de solapamientos y disponibilidad.
 * 
 * @author TurnApp Team
 */
public interface IAsignacionService {
    
    /**
     * Crea una nueva asignación de turno con validación de solapamientos.
     * Esta es la operación principal del servicio.
     * 
     * Validaciones realizadas:
     * - El turno existe y está activo
     * - No hay solapamientos con otras asignaciones del usuario en la misma fecha
     * - (Opcional) La asignación coincide con la disponibilidad preferencial del usuario
     * 
     * @param request DTO con la información de la asignación
     * @return DTO con la información de la asignación creada
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si el turno no existe
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException si hay conflictos o validaciones fallidas
     */
    AsignacionResponse crearAsignacion(AsignacionRequest request);
    
    /**
     * Cancela una asignación de turno existente.
     * No elimina el registro, sino que cambia su estado a CANCELADO.
     * 
     * @param id ID de la asignación a cancelar
     * @param motivo Razón de la cancelación
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si la asignación no existe
     */
    void cancelarAsignacion(Long id, String motivo);
    
    /**
     * Marca una asignación como completada.
     * 
     * @param id ID de la asignación
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si la asignación no existe
     */
    void completarAsignacion(Long id);
    
    /**
     * Obtiene una asignación por su ID.
     * 
     * @param id ID de la asignación
     * @return DTO con la información de la asignación
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si la asignación no existe
     */
    AsignacionResponse obtenerAsignacionPorId(Long id);
    
    /**
     * Obtiene todas las asignaciones de un usuario específico.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de DTOs con las asignaciones del usuario
     */
    List<AsignacionResponse> obtenerAsignacionesPorUsuario(String usuarioId);
    
    /**
     * Obtiene todas las asignaciones en una fecha específica.
     * 
     * @param fecha Fecha de las asignaciones
     * @return Lista de DTOs con las asignaciones en la fecha
     */
    List<AsignacionResponse> obtenerAsignacionesPorFecha(LocalDate fecha);
    
    /**
     * Obtiene las asignaciones de un usuario en un rango de fechas.
     * 
     * @param usuarioId ID del usuario
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de DTOs con las asignaciones en el rango
     */
    List<AsignacionResponse> obtenerAsignacionesPorUsuarioYRangoFechas(
            String usuarioId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
