package com.turnapp.microservice.turnos_microservice.descanso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.turnapp.microservice.turnos_microservice.descanso.model.Descanso;

import java.util.List;

/**
 * Repositorio para la gestión de descansos dentro de asignaciones de turnos.
 * Permite registrar y consultar pausas (almuerzo, café, etc.) durante los turnos.
 * 
 * @author TurnApp Team
 */
@Repository
public interface DescansoRepository extends JpaRepository<Descanso, Long> {
    
    /**
     * Busca todos los descansos asociados a una asignación específica.
     * Útil para ver todas las pausas registradas en un turno.
     * 
     * @param asignacionId ID de la asignación de turno
     * @return Lista de descansos de la asignación
     */
    @Query("SELECT d FROM Descanso d WHERE d.asignacion.id = :asignacionId")
    List<Descanso> findByAsignacionId(@Param("asignacionId") Long asignacionId);
    
    /**
     * Busca descansos por tipo.
     * Útil para análisis de patrones de descanso.
     * 
     * @param tipo Tipo de descanso (ej. "Almuerzo", "Café", "Pausa Activa")
     * @return Lista de descansos del tipo especificado
     */
    List<Descanso> findByTipo(String tipo);
    
    /**
     * Cuenta los descansos de una asignación específica.
     * 
     * @param asignacionId ID de la asignación
     * @return Número de descansos registrados
     */
    @Query("SELECT COUNT(d) FROM Descanso d WHERE d.asignacion.id = :asignacionId")
    long countByAsignacionId(@Param("asignacionId") Long asignacionId);
    
    /**
     * Elimina todos los descansos de una asignación.
     * Útil cuando se cancela una asignación o se necesita limpiar registros.
     * 
     * @param asignacionId ID de la asignación
     */
    @Query("DELETE FROM Descanso d WHERE d.asignacion.id = :asignacionId")
    void deleteByAsignacionId(@Param("asignacionId") Long asignacionId);
}
