package com.turnapp.microservice.turnos_microservice.disponibilidad.service;

import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadRequest;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.UsuarioDisponibleResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.model.DiaSemana;

import java.time.LocalDate;
import java.time.LocalTime;
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
     * Valida si un horario está dentro de la preferencia horaria de un usuario para un día específico.
     * 
     * SISTEMA HÍBRIDO:
     * - Si el usuario NO tiene disponibilidad configurada → Retorna TRUE (disponible por defecto)
     * - Si el usuario tiene disponibilidad configurada → Valida que el turno esté dentro del rango
     * 
     * Este método NO bloquea asignaciones, solo informa si coincide con las preferencias.
     * Es responsabilidad del servicio de asignaciones decidir si emite advertencia o bloquea.
     * 
     * @param usuarioId ID del usuario
     * @param diaSemana Día de la semana
     * @param horaInicio Hora de inicio del turno a validar
     * @param horaFin Hora de fin del turno a validar
     * @return true si está dentro de la preferencia (o no tiene restricciones), false si está fuera
     */
    boolean validarDisponibilidad(String usuarioId, DiaSemana diaSemana, 
                                   LocalTime horaInicio, LocalTime horaFin);
    
    /**
     * Obtiene todos los usuarios que tienen disponibilidad configurada en un rango de fechas.
     * Útil para calcular empleados disponibles en el microservicio de Horarios.
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de DTOs con las disponibilidades en el rango
     */
    List<DisponibilidadResponse> obtenerDisponibilidadesPorPeriodo(
            LocalDate fechaInicio, 
            LocalDate fechaFin
    );
    
    /**
     * Obtiene la lista de usuarios disponibles para un día y horario específico.
     * 
     * LÓGICA HÍBRIDA:
     * - Obtiene TODOS los usuarios del microservicio de usuarios
     * - Por defecto, todos están disponibles
     * - Si tienen preferencias configuradas, valida si el horario cumple con ellas
     * - Excluye usuarios que ya tienen asignaciones activas en la fecha especificada
     * - Retorna información sobre si cumplen o no sus preferencias
     * 
     * @param fecha Fecha específica para verificar asignaciones
     * @param diaSemana Día de la semana a consultar
     * @param horaInicio Hora de inicio del turno
     * @param horaFin Hora de fin del turno
     * @return Lista de usuarios con información de su disponibilidad
     */
    List<UsuarioDisponibleResponse> obtenerUsuariosDisponibles(
            LocalDate fecha,
            DiaSemana diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin
    );
}
