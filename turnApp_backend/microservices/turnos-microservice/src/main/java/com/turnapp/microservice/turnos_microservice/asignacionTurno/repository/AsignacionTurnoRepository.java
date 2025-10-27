package com.turnapp.microservice.turnos_microservice.asignacionTurno.repository;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.EstadoAsignacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la gestión de asignaciones de turnos.
 * Contiene las consultas clave para validación de solapamientos y gestión de turnos asignados.
 * 
 * @author TurnApp Team
 */
@Repository
public interface AsignacionTurnoRepository extends JpaRepository<AsignacionTurno, Long> {

    /**
     * ¡CONSULTA CLAVE PARA VALIDACIÓN DE SOLAPAMIENTOS!
     * 
     * Busca todas las asignaciones de un usuario en una fecha específica,
     * excluyendo las que están en estado cancelado.
     * Esta consulta es fundamental para detectar conflictos de horario.
     * 
     * @param usuarioId ID del usuario
     * @param fecha Fecha de la asignación
     * @param estado Estado a excluir (típicamente CANCELADO)
     * @return Lista de asignaciones activas del usuario en la fecha especificada
     */
    List<AsignacionTurno> findByUsuarioIdAndFechaAndEstadoNot(
            String usuarioId,
            LocalDate fecha,
            EstadoAsignacion estado
    );

    /**
     * Busca todas las asignaciones de un usuario específico.
     * Útil para obtener el historial completo de turnos.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de todas las asignaciones del usuario
     */
    List<AsignacionTurno> findByUsuarioId(String usuarioId);

    /**
     * Busca todas las asignaciones en una fecha específica.
     * Útil para el dashboard de administración y planificación.
     * 
     * @param fecha Fecha de la asignación
     * @return Lista de asignaciones en la fecha especificada
     */
    List<AsignacionTurno> findByFecha(LocalDate fecha);
    
    /**
     * Busca asignaciones por usuario y estado.
     * Útil para filtrar turnos pendientes, completados, etc.
     * 
     * @param usuarioId ID del usuario
     * @param estado Estado de la asignación
     * @return Lista de asignaciones que coinciden con los criterios
     */
    List<AsignacionTurno> findByUsuarioIdAndEstado(String usuarioId, EstadoAsignacion estado);
    
    /**
     * Busca asignaciones por rango de fechas.
     * Útil para reportes y análisis de periodos específicos.
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de asignaciones en el rango especificado
     */
    List<AsignacionTurno> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Busca asignaciones por usuario en un rango de fechas.
     * Útil para obtener el historial de un usuario en un periodo específico.
     * 
     * @param usuarioId ID del usuario
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de asignaciones del usuario en el rango especificado
     */
    List<AsignacionTurno> findByUsuarioIdAndFechaBetween(
            String usuarioId, 
            LocalDate fechaInicio, 
            LocalDate fechaFin
    );
    
    /**
     * Cuenta las asignaciones de un usuario en un estado específico.
     * Útil para estadísticas y reportes.
     * 
     * @param usuarioId ID del usuario
     * @param estado Estado de la asignación
     * @return Número de asignaciones
     */
    long countByUsuarioIdAndEstado(String usuarioId, EstadoAsignacion estado);
    
    /**
     * Consulta personalizada para obtener asignaciones con sus turnos.
     * Optimiza las consultas evitando el problema N+1.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de asignaciones con turnos cargados
     */
    @Query("SELECT a FROM AsignacionTurno a JOIN FETCH a.turno WHERE a.usuarioId = :usuarioId")
    List<AsignacionTurno> findByUsuarioIdWithTurno(@Param("usuarioId") String usuarioId);
}
