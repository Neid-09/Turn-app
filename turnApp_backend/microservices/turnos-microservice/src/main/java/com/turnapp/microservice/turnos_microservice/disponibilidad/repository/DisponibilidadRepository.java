package com.turnapp.microservice.turnos_microservice.disponibilidad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turnapp.microservice.turnos_microservice.disponibilidad.model.Disponibilidad;
import com.turnapp.microservice.turnos_microservice.disponibilidad.model.DiaSemana;

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
     * @param diaSemana Día de la semana (enum DiaSemana)
     * @return Optional con la disponibilidad si existe
     */
    Optional<Disponibilidad> findByUsuarioIdAndDiaSemana(String usuarioId, DiaSemana diaSemana);

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
    List<Disponibilidad> findByDiaSemana(DiaSemana diaSemana);
    
    /**
     * Verifica si existe una regla de disponibilidad para un usuario en un día específico.
     * 
     * @param usuarioId ID del usuario
     * @param diaSemana Día de la semana
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsuarioIdAndDiaSemana(String usuarioId, DiaSemana diaSemana);
    
    /**
     * Busca disponibilidades para múltiples días de la semana y un estado específico.
     * Útil para obtener disponibilidades en un período con múltiples días.
     * 
     * @param diasSemana Lista de días de la semana
     * @param activo Estado de activación de las reglas
     * @return Lista de disponibilidades que coinciden con los criterios
     */
    List<Disponibilidad> findByDiaSemanaInAndActivo(List<DiaSemana> diasSemana, boolean activo);
    
    /**
     * Elimina todas las reglas de disponibilidad de un usuario.
     * Útil cuando un usuario necesita reconfigurar completamente su disponibilidad.
     * 
     * @param usuarioId ID del usuario
     */
    void deleteByUsuarioId(String usuarioId);
}
