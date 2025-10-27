package com.turnapp.microservice.turnos_microservice.disponibilidad.service;

import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadRequest;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadResponse;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de disponibilidad preferencial de usuarios.
 * Define las operaciones para configurar y consultar horarios de preferencia.
 * 
 * @author TurnApp Team
 */
public interface IDisponibilidadService {
    
    /**
     * Crea una nueva regla de disponibilidad para un usuario.
     * 
     * @param request DTO con la información de disponibilidad
     * @return DTO con la información de la disponibilidad creada
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException si ya existe una regla para ese día
     */
    DisponibilidadResponse crearDisponibilidad(DisponibilidadRequest request);
    
    /**
     * Actualiza una regla de disponibilidad existente.
     * 
     * @param id ID de la disponibilidad
     * @param request DTO con la información actualizada
     * @return DTO con la información de la disponibilidad actualizada
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si la disponibilidad no existe
     */
    DisponibilidadResponse actualizarDisponibilidad(Long id, DisponibilidadRequest request);
    
    /**
     * Obtiene una disponibilidad por su ID.
     * 
     * @param id ID de la disponibilidad
     * @return DTO con la información de la disponibilidad
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si no existe
     */
    DisponibilidadResponse obtenerDisponibilidadPorId(Long id);
    
    /**
     * Obtiene todas las reglas de disponibilidad de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de DTOs con las disponibilidades del usuario
     */
    List<DisponibilidadResponse> obtenerDisponibilidadesPorUsuario(String usuarioId);
    
    /**
     * Obtiene las reglas activas de disponibilidad de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de DTOs con las disponibilidades activas
     */
    List<DisponibilidadResponse> obtenerDisponibilidadesActivasPorUsuario(String usuarioId);
    
    /**
     * Elimina una regla de disponibilidad.
     * 
     * @param id ID de la disponibilidad a eliminar
     * @throws com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException si no existe
     */
    void eliminarDisponibilidad(Long id);
    
    /**
     * Valida si un horario está dentro de la disponibilidad de un usuario para un día específico.
     * 
     * @param usuarioId ID del usuario
     * @param diaSemana Día de la semana
     * @param horaInicio Hora de inicio del turno a validar
     * @param horaFin Hora de fin del turno a validar
     * @return true si está dentro de la disponibilidad, false en caso contrario
     */
    boolean validarDisponibilidad(String usuarioId, DayOfWeek diaSemana, 
                                   java.time.LocalTime horaInicio, java.time.LocalTime horaFin);
}
