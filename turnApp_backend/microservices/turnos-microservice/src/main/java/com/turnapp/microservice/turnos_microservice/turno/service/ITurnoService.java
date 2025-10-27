package com.turnapp.microservice.turnos_microservice.turno.service;

import com.turnapp.microservice.turnos_microservice.turno.dto.TurnoRequest;
import com.turnapp.microservice.turnos_microservice.turno.dto.TurnoResponse;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de plantillas de turno.
 * Define las operaciones de negocio disponibles para turnos.
 * 
 * @author TurnApp Team
 */
public interface ITurnoService {
    
    /**
     * Crea una nueva plantilla de turno.
     * 
     * @param request DTO con la información del turno a crear
     * @return DTO con la información del turno creado
     */
    TurnoResponse crearTurno(TurnoRequest request);
    
    /**
     * Actualiza una plantilla de turno existente.
     * 
     * @param id ID del turno a actualizar
     * @param request DTO con la información actualizada
     * @return DTO con la información del turno actualizado
     */
    TurnoResponse actualizarTurno(Long id, TurnoRequest request);
    
    /**
     * Obtiene un turno por su ID.
     * 
     * @param id ID del turno
     * @return DTO con la información del turno
     */
    TurnoResponse obtenerTurnoPorId(Long id);
    
    /**
     * Obtiene todos los turnos.
     * 
     * @return Lista de DTOs con la información de todos los turnos
     */
    List<TurnoResponse> obtenerTodosTurnos();
    
    /**
     * Obtiene todos los turnos activos.
     * 
     * @return Lista de DTOs con turnos activos
     */
    List<TurnoResponse> obtenerTurnosActivos();
    
    /**
     * Elimina (o desactiva) un turno.
     * 
     * @param id ID del turno a eliminar
     */
    void eliminarTurno(Long id);
}
