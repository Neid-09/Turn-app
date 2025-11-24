package com.turnapp.microservice.horarios_microservice.horario.service;

import com.turnapp.microservice.horarios_microservice.horario.dto.*;
import com.turnapp.microservice.horarios_microservice.horario.model.*;
import com.turnapp.microservice.horarios_microservice.horario.repository.HorarioDetalleRepository;
import com.turnapp.microservice.horarios_microservice.horario.repository.HorarioRepository;
import com.turnapp.microservice.horarios_microservice.integration.turnos.client.TurnosClient;
import com.turnapp.microservice.horarios_microservice.integration.turnos.dto.*;
import com.turnapp.microservice.horarios_microservice.integration.usuario.client.UsuarioBasicoResponse;
import com.turnapp.microservice.horarios_microservice.integration.usuario.client.UsuarioClient;
import com.turnapp.microservice.horarios_microservice.shared.exception.*;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de horarios.
 * 
 * RESPONSABILIDADES:
 * - Gestión CRUD de horarios y sus detalles
 * - Validación de reglas de negocio
 * - Publicación síncrona a turnos-microservice
 * - Consolidación de vistas con datos de turnos
 * 
 * ESTRATEGIA DE SINCRONIZACIÓN:
 * - ACTUAL: Síncrona mediante Feign (una asignación a la vez)
 * - FUTURO: Migrar a eventos asíncronos (Kafka/RabbitMQ) para escalabilidad
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HorarioServiceImpl implements IHorarioService {
    
    private final HorarioRepository horarioRepository;
    private final HorarioDetalleRepository detalleRepository;
    private final TurnosClient turnosClient;
    private final UsuarioClient usuarioClient;
    
    // ================== OPERACIONES DE HORARIOS ==================
    
    @Override
    public HorarioResponse crearHorario(HorarioRequest request, String creadoPor) {
        log.info("Creando horario: {} por usuario: {}", request.getNombre(), creadoPor);
        
        // Validar período
        validarPeriodoHorario(request.getFechaInicio(), request.getFechaFin());
        
        // Crear entidad
        Horario horario = Horario.builder()
                .nombre(request.getNombre())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .descripcion(request.getDescripcion())
                .creadoPor(creadoPor)
                .estado(EstadoHorario.BORRADOR)
                .build();
        
        horario = horarioRepository.save(horario);
        
        log.info("Horario creado exitosamente con ID: {}", horario.getId());
        return mapearAResponse(horario, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public HorarioResponse obtenerHorarioPorId(Long id, boolean incluirDetalles) {
        log.debug("Obteniendo horario ID: {}, incluirDetalles: {}", id, incluirDetalles);
        
        Horario horario = buscarHorarioPorId(id);
        return mapearAResponse(horario, incluirDetalles);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponse> listarHorarios() {
        log.debug("Listando todos los horarios");
        
        return horarioRepository.findAllByOrderByFechaInicioDesc()
                .stream()
                .map(h -> mapearAResponse(h, false))
                .collect(Collectors.toList());
    }
    
    @Override
    public HorarioResponse actualizarHorario(Long id, HorarioRequest request) {
        log.info("Actualizando horario ID: {}", id);
        
        Horario horario = buscarHorarioPorId(id);
        
        // Validar que esté en estado BORRADOR
        if (!horario.esEditable()) {
            throw new BusinessLogicException(
                "No se puede actualizar un horario en estado " + horario.getEstado() + 
                ". Solo los horarios en estado BORRADOR pueden ser modificados."
            );
        }
        
        // Validar nuevo período
        validarPeriodoHorario(request.getFechaInicio(), request.getFechaFin());
        
        // Actualizar campos
        horario.setNombre(request.getNombre());
        horario.setFechaInicio(request.getFechaInicio());
        horario.setFechaFin(request.getFechaFin());
        horario.setDescripcion(request.getDescripcion());
        
        horario = horarioRepository.save(horario);
        
        log.info("Horario actualizado exitosamente");
        return mapearAResponse(horario, false);
    }
    
    @Override
    public void eliminarHorario(Long id) {
        log.info("Eliminando horario ID: {}", id);
        
        Horario horario = buscarHorarioPorId(id);
        
        // Validar que esté en estado BORRADOR
        if (!horario.esEditable()) {
            throw new BusinessLogicException(
                "No se puede eliminar un horario en estado " + horario.getEstado() + 
                ". Solo los horarios en estado BORRADOR pueden ser eliminados."
            );
        }
        
        horarioRepository.delete(horario);
        log.info("Horario eliminado exitosamente");
    }
    
    // ================== OPERACIONES DE DETALLES ==================
    
    @Override
    public HorarioDetalleResponse agregarDetalle(Long horarioId, HorarioDetalleRequest request) {
        log.info("Agregando detalle al horario ID: {}", horarioId);
        
        Horario horario = buscarHorarioPorId(horarioId);
        
        // Validar que esté en estado BORRADOR
        if (!horario.esEditable()) {
            throw new BusinessLogicException(
                "No se pueden agregar detalles a un horario en estado " + horario.getEstado()
            );
        }
        
        // Validar que la fecha esté dentro del período del horario
        validarFechaEnPeriodo(request.getFecha(), horario);
        
        // Consultar información del turno desde turnos-microservice
        String nombreTurno = obtenerNombreTurno(request.getTurnoId());
        
        // Crear detalle
        HorarioDetalle detalle = HorarioDetalle.builder()
                .usuarioId(request.getUsuarioId())
                .fecha(request.getFecha())
                .turnoId(request.getTurnoId())
                .nombreTurno(nombreTurno)
                .observaciones(request.getObservaciones())
                .estado(EstadoDetalle.PLANIFICADO)
                .build();
        
        horario.agregarDetalle(detalle);
        horarioRepository.save(horario);
        
        log.info("Detalle agregado exitosamente con ID: {}", detalle.getId());
        return mapearDetalleAResponse(detalle);
    }
    
    @Override
    public List<HorarioDetalleResponse> agregarDetallesEnLote(Long horarioId, List<HorarioDetalleRequest> requests) {
        log.info("Agregando {} detalles al horario ID: {}", requests.size(), horarioId);
        
        List<HorarioDetalleResponse> responses = new ArrayList<>();
        
        for (HorarioDetalleRequest request : requests) {
            try {
                HorarioDetalleResponse response = agregarDetalle(horarioId, request);
                responses.add(response);
            } catch (Exception e) {
                log.warn("Error al agregar detalle: {}", e.getMessage());
                // Continuar con los siguientes detalles
            }
        }
        
        log.info("Agregados {} detalles exitosamente de {}", responses.size(), requests.size());
        return responses;
    }
    
    @Override
    public void eliminarDetalle(Long horarioId, Long detalleId) {
        log.info("Eliminando detalle ID: {} del horario ID: {}", detalleId, horarioId);
        
        Horario horario = buscarHorarioPorId(horarioId);
        
        // Validar que esté en estado BORRADOR
        if (!horario.esEditable()) {
            throw new BusinessLogicException(
                "No se pueden eliminar detalles de un horario en estado " + horario.getEstado()
            );
        }
        
        HorarioDetalle detalle = detalleRepository.findById(detalleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Detalle no encontrado con ID: " + detalleId
                ));
        
        // Validar que el detalle pertenezca al horario
        if (!detalle.getHorario().getId().equals(horarioId)) {
            throw new BusinessLogicException(
                "El detalle no pertenece al horario especificado"
            );
        }
        
        horario.removerDetalle(detalle);
        horarioRepository.save(horario);
        
        log.info("Detalle eliminado exitosamente");
    }
    
    // ================== PUBLICACIÓN ==================
    
    @Override
    public ReportePublicacionResponse publicarHorario(Long id) {
        log.info("📢 Iniciando publicación del horario ID: {}", id);
        
        Horario horario = buscarHorarioPorId(id);
        
        // Validar que el horario pueda ser publicado
        if (!horario.esPublicable()) {
            throw new BusinessLogicException(
                "El horario no puede ser publicado. Estado actual: " + horario.getEstado() + 
                ", Detalles: " + horario.getDetalles().size()
            );
        }
        
        // Inicializar reporte
        ReportePublicacionResponse reporte = ReportePublicacionResponse.builder()
                .horarioId(horario.getId())
                .nombreHorario(horario.getNombre())
                .totalProcesados(horario.getDetalles().size())
                .totalExitosos(0)
                .totalFallidos(0)
                .asignacionesExitosas(new ArrayList<>())
                .asignacionesFallidas(new ArrayList<>())
                .build();
        
        // Publicar cada detalle (SÍNCRONO)
        // TODO ESCALABILIDAD: Migrar a eventos asíncronos (Kafka) para volúmenes altos
        for (HorarioDetalle detalle : horario.getDetalles()) {
            try {
                publicarDetalle(detalle, reporte);
            } catch (Exception e) {
                log.error("Error al publicar detalle ID: {}", detalle.getId(), e);
                registrarFallo(detalle, e.getMessage(), reporte);
            }
        }
        
        // Actualizar estado del horario
        horario.setEstado(EstadoHorario.PUBLICADO);
        horario.setPublicadoEn(LocalDateTime.now());
        horarioRepository.save(horario);
        
        log.info("✅ Publicación completada. Exitosos: {}, Fallidos: {}", 
                 reporte.getTotalExitosos(), reporte.getTotalFallidos());
        
        return reporte;
    }
    
    /**
     * Publica un detalle individual creando la asignación en turnos-microservice.
     */
    private void publicarDetalle(HorarioDetalle detalle, ReportePublicacionResponse reporte) {
        log.debug("Publicando detalle ID: {} - Usuario: {}, Fecha: {}, Turno: {}", 
                  detalle.getId(), detalle.getUsuarioId(), detalle.getFecha(), detalle.getTurnoId());
        
        // Crear request para turnos-microservice
        AsignacionRequest request = AsignacionRequest.builder()
                .usuarioId(detalle.getUsuarioId())
                .turnoId(detalle.getTurnoId())
                .fecha(detalle.getFecha())
                .observaciones(detalle.getObservaciones())
                .build();
        
        try {
            // Llamar a turnos-microservice vía Feign (SÍNCRONO)
            AsignacionResponse asignacion = turnosClient.crearAsignacion(request);
            
            // Marcar detalle como confirmado
            detalle.marcarComoConfirmado(asignacion.getId());
            detalleRepository.save(detalle);
            
            // Registrar éxito en reporte
            registrarExito(detalle, asignacion, reporte);
            
            log.debug("✅ Detalle publicado exitosamente. AsignacionId: {}", asignacion.getId());
            
        } catch (FeignException.ServiceUnavailable e) {
            throw new MicroserviceUnavailableException(
                "turnos-microservice",
                "El microservicio de turnos no está disponible",
                e
            );
        } catch (FeignException e) {
            String mensajeError = extraerMensajeError(e);
            throw new BusinessLogicException(
                "Error al crear asignación en turnos-microservice: " + mensajeError,
                e
            );
        }
    }
    
    private void registrarExito(HorarioDetalle detalle, AsignacionResponse asignacion, 
                                ReportePublicacionResponse reporte) {
        reporte.setTotalExitosos(reporte.getTotalExitosos() + 1);
        reporte.getAsignacionesExitosas().add(
            ReportePublicacionResponse.AsignacionExitosa.builder()
                    .detalleId(detalle.getId())
                    .asignacionId(asignacion.getId())
                    .usuarioId(detalle.getUsuarioId())
                    .fecha(detalle.getFecha().toString())
                    .nombreTurno(detalle.getNombreTurno())
                    .build()
        );
    }
    
    private void registrarFallo(HorarioDetalle detalle, String motivoError, 
                               ReportePublicacionResponse reporte) {
        reporte.setTotalFallidos(reporte.getTotalFallidos() + 1);
        reporte.getAsignacionesFallidas().add(
            ReportePublicacionResponse.AsignacionFallida.builder()
                    .detalleId(detalle.getId())
                    .usuarioId(detalle.getUsuarioId())
                    .fecha(detalle.getFecha().toString())
                    .turnoId(detalle.getTurnoId())
                    .motivoError(motivoError)
                    .build()
        );
    }
    
    // ================== CONSULTAS CONSOLIDADAS ==================
    
    @Override
    @Transactional(readOnly = true)
    public HorarioConsolidadoResponse obtenerHorarioConsolidado(Long id) {
        log.info("Generando vista consolidada del horario ID: {}", id);
        
        Horario horario = buscarHorarioPorId(id);
        
        // Obtener asignaciones reales desde turnos-microservice
        List<AsignacionResponse> asignaciones;
        try {
            asignaciones = turnosClient.obtenerAsignacionesPorPeriodo(
                horario.getFechaInicio(),
                horario.getFechaFin()
            );
        } catch (FeignException e) {
            throw new MicroserviceUnavailableException(
                "turnos-microservice",
                "No se pudo obtener las asignaciones desde turnos-microservice",
                e
            );
        }
        
        // Organizar asignaciones por fecha
        Map<LocalDate, List<AsignacionResponse>> asignacionesPorFecha = asignaciones.stream()
                .collect(Collectors.groupingBy(AsignacionResponse::getFecha));
        
        // Calcular estadísticas
        HorarioConsolidadoResponse.EstadisticasHorario estadisticas = calcularEstadisticas(
            horario, asignaciones
        );
        
        return HorarioConsolidadoResponse.builder()
                .horario(mapearAResponse(horario, false))
                .asignacionesPorFecha(asignacionesPorFecha)
                .estadisticas(estadisticas)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponse> buscarHorariosPorFecha(LocalDate fecha) {
        log.debug("Buscando horarios que cubren la fecha: {}", fecha);
        
        return horarioRepository.findByFechaEnPeriodo(fecha)
                .stream()
                .map(h -> mapearAResponse(h, false))
                .collect(Collectors.toList());
    }
    
    // ================== MÉTODOS AUXILIARES ==================
    
    private Horario buscarHorarioPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Horario no encontrado con ID: " + id
                ));
    }
    
    private void validarPeriodoHorario(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidationException(
                "La fecha de inicio no puede ser posterior a la fecha de fin"
            );
        }
    }
    
    private void validarFechaEnPeriodo(LocalDate fecha, Horario horario) {
        if (fecha.isBefore(horario.getFechaInicio()) || fecha.isAfter(horario.getFechaFin())) {
            throw new ValidationException(
                String.format("La fecha %s está fuera del período del horario (%s - %s)",
                    fecha, horario.getFechaInicio(), horario.getFechaFin())
            );
        }
    }
    
    private String obtenerNombreTurno(Long turnoId) {
        try {
            List<TurnoResponse> turnos = turnosClient.obtenerTurnosActivos();
            return turnos.stream()
                    .filter(t -> t.getId().equals(turnoId))
                    .findFirst()
                    .map(TurnoResponse::getNombre)
                    .orElse("Turno desconocido");
        } catch (Exception e) {
            log.warn("No se pudo obtener el nombre del turno ID: {}", turnoId);
            return "Turno " + turnoId;
        }
    }
    
    private HorarioConsolidadoResponse.EstadisticasHorario calcularEstadisticas(
            Horario horario, List<AsignacionResponse> asignaciones) {
        
        int totalPlanificadas = horario.getDetalles().size();
        long totalConfirmadas = horario.getDetalles().stream()
                .filter(HorarioDetalle::estaSincronizado)
                .count();
        
        long totalCompletadas = asignaciones.stream()
                .filter(a -> a.getEstado() == EstadoAsignacion.COMPLETADO)
                .count();
        
        long totalCanceladas = asignaciones.stream()
                .filter(a -> a.getEstado() == EstadoAsignacion.CANCELADO)
                .count();
        
        double porcentajeSincronizacion = totalPlanificadas > 0 
                ? (totalConfirmadas * 100.0) / totalPlanificadas 
                : 0.0;
        
        return HorarioConsolidadoResponse.EstadisticasHorario.builder()
                .totalPlanificadas(totalPlanificadas)
                .totalConfirmadas((int) totalConfirmadas)
                .totalCompletadas((int) totalCompletadas)
                .totalCanceladas((int) totalCanceladas)
                .porcentajeSincronizacion(porcentajeSincronizacion)
                .build();
    }
    
    private String extraerMensajeError(FeignException e) {
        String mensaje = e.getMessage();
        // Intentar extraer mensaje del JSON de error
        if (mensaje != null && mensaje.contains("\"message\"")) {
            int inicio = mensaje.indexOf("\"message\":\"") + 11;
            int fin = mensaje.indexOf("\"", inicio);
            if (fin > inicio) {
                return mensaje.substring(inicio, fin);
            }
        }
        return mensaje != null ? mensaje : "Error desconocido";
    }
    
    // ================== MAPPERS ==================
    
    private HorarioResponse mapearAResponse(Horario horario, boolean incluirDetalles) {
        HorarioResponse.HorarioResponseBuilder builder = HorarioResponse.builder()
                .id(horario.getId())
                .nombre(horario.getNombre())
                .fechaInicio(horario.getFechaInicio())
                .fechaFin(horario.getFechaFin())
                .estado(horario.getEstado())
                .descripcion(horario.getDescripcion())
                .creadoPor(horario.getCreadoPor())
                .creadoEn(horario.getCreadoEn())
                .actualizadoEn(horario.getActualizadoEn())
                .publicadoEn(horario.getPublicadoEn())
                .cantidadDetalles(horario.getDetalles().size());
        
        if (incluirDetalles) {
            List<HorarioDetalleResponse> detalles = horario.getDetalles().stream()
                    .map(this::mapearDetalleAResponse)
                    .collect(Collectors.toList());
            builder.detalles(detalles);
        }
        
        return builder.build();
    }
    
    private HorarioDetalleResponse mapearDetalleAResponse(HorarioDetalle detalle) {
        // Obtener el nombre del empleado desde usuarios-microservice
        String nombreEmpleado = null;
        try {
            UsuarioBasicoResponse usuario = usuarioClient.obtenerUsuarioPorKeycloakId(detalle.getUsuarioId());
            if (usuario != null) {
                nombreEmpleado = String.format("%s %s", 
                    usuario.getFirstName() != null ? usuario.getFirstName() : "",
                    usuario.getLastName() != null ? usuario.getLastName() : ""
                ).trim();
            }
        } catch (FeignException.NotFound e) {
            log.warn("Usuario no encontrado: {}", detalle.getUsuarioId());
            nombreEmpleado = "Usuario no encontrado";
        } catch (Exception e) {
            log.error("Error al obtener datos del usuario {}: {}", detalle.getUsuarioId(), e.getMessage());
            nombreEmpleado = "Error al cargar nombre";
        }
        
        return HorarioDetalleResponse.builder()
                .id(detalle.getId())
                .horarioId(detalle.getHorario().getId())
                .usuarioId(detalle.getUsuarioId())
                .nombreEmpleado(nombreEmpleado)
                .fecha(detalle.getFecha())
                .turnoId(detalle.getTurnoId())
                .nombreTurno(detalle.getNombreTurno())
                .asignacionId(detalle.getAsignacionId())
                .estado(detalle.getEstado())
                .observaciones(detalle.getObservaciones())
                .creadoEn(detalle.getCreadoEn())
                .actualizadoEn(detalle.getActualizadoEn())
                .confirmadoEn(detalle.getConfirmadoEn())
                .build();
    }
}
