package com.turnapp.microservice.turnos_microservice.turno.repository;

import com.turnapp.microservice.turnos_microservice.turno.model.EstadoTurno;
import com.turnapp.microservice.turnos_microservice.turno.model.Turno;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de plantillas de turnos.
 * Proporciona acceso a datos de la entidad Turno.
 * 
 * @author TurnApp Team
 */
@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    
    /**
     * Busca todas las plantillas de turno por estado.
     * Útil para filtrar turnos activos que pueden ser asignados.
     * 
     * @param estado Estado del turno (ACTIVO/INACTIVO)
     * @return Lista de turnos con el estado especificado
     */
    List<Turno> findByEstado(EstadoTurno estado);
    
    /**
     * Busca un turno por su nombre.
     * Útil para evitar duplicados o buscar turnos específicos.
     * 
     * @param nombre Nombre del turno
     * @return Lista de turnos con el nombre especificado
     */
    List<Turno> findByNombre(String nombre);
    
    /**
     * Verifica si existe un turno con el nombre especificado.
     * 
     * @param nombre Nombre del turno
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
