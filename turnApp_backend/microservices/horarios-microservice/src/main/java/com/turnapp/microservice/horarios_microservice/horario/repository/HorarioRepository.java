package com.turnapp.microservice.horarios_microservice.horario.repository;

import com.turnapp.microservice.horarios_microservice.horario.model.Horario;
import com.turnapp.microservice.horarios_microservice.horario.model.EstadoHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad Horario.
 * 
 * @author TurnApp Team
 */
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    
    /**
     * Encuentra horarios por estado.
     * 
     * @param estado Estado del horario
     * @return Lista de horarios en el estado especificado
     */
    List<Horario> findByEstado(EstadoHorario estado);
    
    /**
     * Encuentra horarios que cubren una fecha específica.
     * 
     * @param fecha Fecha a verificar
     * @return Lista de horarios que incluyen la fecha
     */
    @Query("SELECT h FROM Horario h WHERE h.fechaInicio <= :fecha AND h.fechaFin >= :fecha")
    List<Horario> findByFechaEnPeriodo(@Param("fecha") LocalDate fecha);
    
    /**
     * Encuentra horarios que se solapan con un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de horarios que se solapan con el rango
     */
    @Query("SELECT h FROM Horario h WHERE h.fechaInicio <= :fechaFin AND h.fechaFin >= :fechaInicio")
    List<Horario> findBySolapamientoFechas(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
    
    /**
     * Encuentra horarios creados por un usuario específico.
     * 
     * @param creadoPor UUID del usuario creador
     * @return Lista de horarios creados por el usuario
     */
    List<Horario> findByCreadoPor(String creadoPor);
    
    /**
     * Encuentra horarios ordenados por fecha de inicio descendente.
     * 
     * @return Lista de horarios ordenados
     */
    List<Horario> findAllByOrderByFechaInicioDesc();
    
    /**
     * Busca horarios por nombre (búsqueda parcial case-insensitive).
     * 
     * @param nombre Nombre a buscar
     * @return Lista de horarios que coinciden
     */
    List<Horario> findByNombreContainingIgnoreCase(String nombre);
}
