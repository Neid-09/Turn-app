package com.turnapp.microservice.horarios_microservice.horario.repository;

import com.turnapp.microservice.horarios_microservice.horario.model.HorarioDetalle;
import com.turnapp.microservice.horarios_microservice.horario.model.EstadoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad HorarioDetalle.
 * 
 * @author TurnApp Team
 */
@Repository
public interface HorarioDetalleRepository extends JpaRepository<HorarioDetalle, Long> {
    
    /**
     * Encuentra todos los detalles de un horario específico.
     * 
     * @param horarioId ID del horario
     * @return Lista de detalles del horario
     */
    List<HorarioDetalle> findByHorarioId(Long horarioId);
    
    /**
     * Encuentra detalles por usuario en un rango de fechas.
     * 
     * @param usuarioId UUID del usuario
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de detalles del usuario en el período
     */
    @Query("SELECT d FROM HorarioDetalle d WHERE d.usuarioId = :usuarioId " +
           "AND d.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<HorarioDetalle> findByUsuarioAndFechaBetween(
        @Param("usuarioId") String usuarioId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
    
    /**
     * Encuentra detalles por estado.
     * 
     * @param estado Estado del detalle
     * @return Lista de detalles en el estado especificado
     */
    List<HorarioDetalle> findByEstado(EstadoDetalle estado);
    
    /**
     * Encuentra detalles no sincronizados (sin asignacionId).
     * 
     * @return Lista de detalles pendientes de sincronización
     */
    @Query("SELECT d FROM HorarioDetalle d WHERE d.asignacionId IS NULL")
    List<HorarioDetalle> findNoSincronizados();
    
    /**
     * Encuentra detalles por ID de asignación en turnos.
     * 
     * @param asignacionId ID de la asignación en turnos-microservice
     * @return Detalle asociado a la asignación
     */
    List<HorarioDetalle> findByAsignacionId(Long asignacionId);
}
