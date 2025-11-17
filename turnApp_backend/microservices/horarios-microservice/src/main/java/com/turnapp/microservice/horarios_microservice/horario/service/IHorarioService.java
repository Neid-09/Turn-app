package com.turnapp.microservice.horarios_microservice.horario.service;

import com.turnapp.microservice.horarios_microservice.horario.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de horarios.
 * 
 * Define las operaciones de negocio disponibles para horarios:
 * - CRUD de horarios
 * - Gestión de detalles
 * - Publicación (sincronización con turnos-microservice)
 * - Consultas consolidadas
 * 
 * @author TurnApp Team
 */
public interface IHorarioService {
    
    // ================== OPERACIONES DE HORARIOS ==================
    
    /**
     * Crea un nuevo horario en estado BORRADOR.
     * 
     * @param request Datos del horario a crear
     * @param creadoPor UUID del usuario creador (extraído del JWT)
     * @return Horario creado
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ValidationException 
     *         si las fechas son inválidas
     */
    HorarioResponse crearHorario(HorarioRequest request, String creadoPor);
    
    /**
     * Obtiene un horario por su ID.
     * 
     * @param id ID del horario
     * @param incluirDetalles Si debe incluir la lista de detalles
     * @return Horario encontrado
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario no existe
     */
    HorarioResponse obtenerHorarioPorId(Long id, boolean incluirDetalles);
    
    /**
     * Lista todos los horarios.
     * 
     * @return Lista de horarios
     */
    List<HorarioResponse> listarHorarios();
    
    /**
     * Actualiza un horario existente.
     * Solo se pueden actualizar horarios en estado BORRADOR.
     * 
     * @param id ID del horario
     * @param request Nuevos datos del horario
     * @return Horario actualizado
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario no existe
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.BusinessLogicException 
     *         si el horario no está en estado BORRADOR
     */
    HorarioResponse actualizarHorario(Long id, HorarioRequest request);
    
    /**
     * Elimina un horario.
     * Solo se pueden eliminar horarios en estado BORRADOR.
     * 
     * @param id ID del horario
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario no existe
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.BusinessLogicException 
     *         si el horario no está en estado BORRADOR
     */
    void eliminarHorario(Long id);
    
    // ================== OPERACIONES DE DETALLES ==================
    
    /**
     * Agrega un detalle (asignación planificada) a un horario.
     * 
     * @param horarioId ID del horario
     * @param request Datos del detalle
     * @return Detalle creado
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario no existe
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.BusinessLogicException 
     *         si el horario no está en estado BORRADOR
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.MicroserviceUnavailableException 
     *         si no se puede consultar información del turno
     */
    HorarioDetalleResponse agregarDetalle(Long horarioId, HorarioDetalleRequest request);
    
    /**
     * Agrega múltiples detalles de forma masiva.
     * 
     * @param horarioId ID del horario
     * @param requests Lista de detalles a agregar
     * @return Lista de detalles creados
     */
    List<HorarioDetalleResponse> agregarDetallesEnLote(Long horarioId, List<HorarioDetalleRequest> requests);
    
    /**
     * Elimina un detalle del horario.
     * Solo se pueden eliminar detalles de horarios en BORRADOR.
     * 
     * @param horarioId ID del horario
     * @param detalleId ID del detalle
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario o detalle no existe
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.BusinessLogicException 
     *         si el horario no está en estado BORRADOR
     */
    void eliminarDetalle(Long horarioId, Long detalleId);
    
    // ================== PUBLICACIÓN ==================
    
    /**
     * Publica un horario, creando las asignaciones en turnos-microservice.
     * 
     * PROCESO:
     * 1. Valida que el horario esté en estado BORRADOR y tenga detalles
     * 2. Para cada detalle:
     *    a. Crea AsignacionRequest
     *    b. Llama a turnos-microservice vía Feign (SÍNCRONO)
     *    c. Guarda el asignacionId retornado
     *    d. Marca el detalle como CONFIRMADO
     * 3. Cambia el estado del horario a PUBLICADO
     * 
     * MANEJO DE ERRORES:
     * - Si falla una asignación, se marca como error pero continúa
     * - Retorna reporte de éxitos y fallos
     * 
     * NOTA DE ESCALABILIDAD:
     * Para volúmenes altos (>100 asignaciones), considerar migrar a
     * patrón asíncrono con eventos (Kafka/RabbitMQ).
     * 
     * @param id ID del horario
     * @return Reporte de publicación
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario no existe
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.BusinessLogicException 
     *         si el horario no puede ser publicado
     */
    ReportePublicacionResponse publicarHorario(Long id);
    
    // ================== CONSULTAS CONSOLIDADAS ==================
    
    /**
     * Obtiene la vista consolidada de un horario.
     * 
     * Combina:
     * - Información del horario local
     * - Asignaciones reales del microservicio de turnos (vía Feign)
     * - Estadísticas calculadas
     * 
     * @param id ID del horario
     * @return Vista consolidada del horario
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.ResourceNotFoundException 
     *         si el horario no existe
     * @throws com.turnapp.microservice.horarios_microservice.shared.exception.MicroserviceUnavailableException 
     *         si turnos-microservice no responde
     */
    HorarioConsolidadoResponse obtenerHorarioConsolidado(Long id);
    
    /**
     * Busca horarios que cubren una fecha específica.
     * 
     * @param fecha Fecha a buscar
     * @return Lista de horarios que incluyen la fecha
     */
    List<HorarioResponse> buscarHorariosPorFecha(LocalDate fecha);
}
