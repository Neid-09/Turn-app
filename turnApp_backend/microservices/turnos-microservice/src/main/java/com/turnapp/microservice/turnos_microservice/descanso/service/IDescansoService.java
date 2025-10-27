package com.turnapp.microservice.turnos_microservice.descanso.service;

import com.turnapp.microservice.turnos_microservice.descanso.dto.DescansoRequest;
import com.turnapp.microservice.turnos_microservice.descanso.dto.DescansoResponse;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de descansos durante turnos.
 * Define las operaciones para registrar pausas durante las asignaciones.
 * 
 * @author TurnApp Team
 */
public interface IDescansoService {
    
    /**
     * Registra un descanso dentro de una asignación de turno.
     * 
     * @param request DTO con la información del descanso
     * @return DTO con la información del descanso registrado
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si la asignación no existe
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException si el horario es inválido
     */
    DescansoResponse registrarDescanso(DescansoRequest request);
    
    /**
     * Obtiene un descanso por su ID.
     * 
     * @param id ID del descanso
     * @return DTO con la información del descanso
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si no existe
     */
    DescansoResponse obtenerDescansoPorId(Long id);
    
    /**
     * Obtiene todos los descansos de una asignación específica.
     * 
     * @param asignacionId ID de la asignación
     * @return Lista de DTOs con los descansos
     */
    List<DescansoResponse> obtenerDescansosPorAsignacion(Long asignacionId);
    
    /**
     * Elimina un descanso.
     * 
     * @param id ID del descanso a eliminar
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si no existe
     */
    void eliminarDescanso(Long id);
}
