package com.turnapp.microservice.turnos_microservice.reemplazoTurno.service;

import com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto.ReemplazoRequest;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto.ReemplazoResponse;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de reemplazos de turno.
 * Define las operaciones para solicitar y aprobar reemplazos.
 * 
 * @author TurnApp Team
 */
public interface IReemplazoService {
    
    /**
     * Crea una solicitud de reemplazo de turno.
     * 
     * @param request DTO con la información del reemplazo
     * @return DTO con la información del reemplazo creado
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si la asignación no existe
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException si hay conflictos
     */
    ReemplazoResponse solicitarReemplazo(ReemplazoRequest request);
    
    /**
     * Aprueba una solicitud de reemplazo.
     * Cambia el estado a APROBADO y actualiza la asignación original.
     * 
     * @param id ID del reemplazo
     * @param aprobadorId ID del administrador que aprueba
     * @return DTO con la información del reemplazo aprobado
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si el reemplazo no existe
     */
    ReemplazoResponse aprobarReemplazo(Long id, Long aprobadorId);
    
    /**
     * Rechaza una solicitud de reemplazo.
     * 
     * @param id ID del reemplazo
     * @param aprobadorId ID del administrador que rechaza
     * @param motivo Motivo del rechazo
     * @return DTO con la información del reemplazo rechazado
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si el reemplazo no existe
     */
    ReemplazoResponse rechazarReemplazo(Long id, Long aprobadorId, String motivo);
    
    /**
     * Obtiene un reemplazo por su ID.
     * 
     * @param id ID del reemplazo
     * @return DTO con la información del reemplazo
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si no existe
     */
    ReemplazoResponse obtenerReemplazoPorId(Long id);
    
    /**
     * Obtiene todas las solicitudes de reemplazo pendientes.
     * 
     * @return Lista de DTOs con reemplazos pendientes
     */
    List<ReemplazoResponse> obtenerReemplazosPendientes();
    
    /**
     * Obtiene todos los reemplazos donde un usuario es el reemplazante.
     * 
     * @param reemplazanteId ID del usuario reemplazante
     * @return Lista de DTOs con los reemplazos
     */
    List<ReemplazoResponse> obtenerReemplazosPorReemplazante(String reemplazanteId);
}
