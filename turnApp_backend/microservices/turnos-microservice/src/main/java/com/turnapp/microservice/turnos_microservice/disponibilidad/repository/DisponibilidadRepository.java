package com.turnapp.microservice.turnos_microservice.disponibilidad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turnapp.microservice.turnos_microservice.disponibilidad.model.Disponibilidad;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de reglas de disponibilidad preferencial de usuarios.
 * Permite definir y consultar los horarios en que los usuarios prefieren o están
 * contratados para trabajar.
 * 
 * @author TurnApp Team
 */
@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    /**
     * Busca la regla de disponibilidad de un usuario para un día específico.
     * Útil para validar si una asignación coincide con la disponibilidad preferencial.
     * 
     * @param usuarioId ID del usuario
     * @param diaSemana Día de la semana (enum DayOfWeek)
     * @return Optional con la disponibilidad si existe
     */
    Optional<Disponibilidad> findByUsuarioIdAndDiaSemana(String usuarioId, DayOfWeek diaSemana);

    /**
     * Busca todas las reglas de disponibilidad de un usuario.
     * Útil para mostrar la configuración completa de disponibilidad.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de todas las reglas de disponibilidad del usuario
     */
    List<Disponibilidad> findByUsuarioId(String usuarioId);
    
    /**
     * Busca todas las reglas de disponibilidad activas de un usuario.
     * Filtra solo las reglas que están habilitadas.
     * 
     * @param usuarioId ID del usuario
     * @param activo Estado de activación de la regla
     * @return Lista de reglas de disponibilidad activas
     */
    List<Disponibilidad> findByUsuarioIdAndActivo(String usuarioId, boolean activo);
    
    /**
     * Busca disponibilidades por día de la semana.
     * Útil para análisis de disponibilidad global por día.
     * 
     * @param diaSemana Día de la semana
     * @return Lista de disponibilidades para el día especificado
     */
    List<Disponibilidad> findByDiaSemana(DayOfWeek diaSemana);
    
    /**
     * Verifica si existe una regla de disponibilidad para un usuario en un día específico.
     * 
     * @param usuarioId ID del usuario
     * @param diaSemana Día de la semana
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsuarioIdAndDiaSemana(String usuarioId, DayOfWeek diaSemana);
    
    /**
     * Elimina todas las reglas de disponibilidad de un usuario.
     * Útil cuando un usuario necesita reconfigurar completamente su disponibilidad.
     * 
     * @param usuarioId ID del usuario
     */
    void deleteByUsuarioId(String usuarioId);
}
