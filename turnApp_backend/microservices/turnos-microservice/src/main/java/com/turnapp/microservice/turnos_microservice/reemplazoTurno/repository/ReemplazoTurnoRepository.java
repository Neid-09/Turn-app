package com.turnapp.microservice.turnos_microservice.reemplazoTurno.repository;

import com.turnapp.microservice.turnos_microservice.reemplazoTurno.model.EstadoReemplazo;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.model.ReemplazoTurno;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la gestión de reemplazos de turnos.
 * Maneja las solicitudes de reemplazo entre usuarios.
 * 
 * @author TurnApp Team
 */
@Repository
public interface ReemplazoTurnoRepository extends JpaRepository<ReemplazoTurno, Long> {
    
    /**
     * Busca todos los reemplazos por estado.
     * Útil para ver solicitudes pendientes de aprobación.
     * 
     * @param estado Estado del reemplazo (PENDIENTE, APROBADO, RECHAZADO)
     * @return Lista de reemplazos con el estado especificado
     */
    List<ReemplazoTurno> findByEstado(EstadoReemplazo estado);
    
    /**
     * Busca reemplazos donde un usuario específico es el reemplazante.
     * Útil para ver qué turnos ha tomado un usuario.
     * 
     * @param reemplazanteId ID del usuario reemplazante
     * @return Lista de reemplazos donde el usuario es el reemplazante
     */
    List<ReemplazoTurno> findByReemplazanteId(String reemplazanteId);
    
    /**
     * Busca reemplazos por fecha.
     * Útil para planificación y dashboard de reemplazos del día.
     * 
     * @param fecha Fecha del reemplazo
     * @return Lista de reemplazos en la fecha especificada
     */
    List<ReemplazoTurno> findByFecha(LocalDate fecha);
    
    /**
     * Busca reemplazos por asignación original.
     * Útil para ver el historial de reemplazos de una asignación específica.
     * 
     * @param asignacionId ID de la asignación original
     * @return Lista de reemplazos asociados a la asignación
     */
    @Query("SELECT r FROM ReemplazoTurno r WHERE r.asignacionOriginal.id = :asignacionId")
    List<ReemplazoTurno> findByAsignacionOriginalId(@Param("asignacionId") Long asignacionId);
    
    /**
     * Busca reemplazos pendientes de aprobación.
     * Atajo conveniente para obtener las solicitudes que requieren atención.
     * 
     * @return Lista de reemplazos pendientes
     */
    default List<ReemplazoTurno> findPendientes() {
        return findByEstado(EstadoReemplazo.PENDIENTE);
    }
    
    /**
     * Busca reemplazos aprobados por un aprobador específico.
     * Útil para auditoría y seguimiento de aprobaciones.
     * 
     * @param aprobadorId ID del aprobador
     * @return Lista de reemplazos aprobados por el usuario
     */
    List<ReemplazoTurno> findByAprobadorId(Long aprobadorId);
    
    /**
     * Cuenta los reemplazos de un usuario en un estado específico.
     * 
     * @param reemplazanteId ID del usuario reemplazante
     * @param estado Estado del reemplazo
     * @return Número de reemplazos
     */
    long countByReemplazanteIdAndEstado(String reemplazanteId, EstadoReemplazo estado);
}
