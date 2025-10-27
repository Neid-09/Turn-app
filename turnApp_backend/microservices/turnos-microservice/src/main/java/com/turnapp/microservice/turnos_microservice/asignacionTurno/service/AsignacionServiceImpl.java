package com.turnapp.microservice.turnos_microservice.asignacionTurno.service;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.dto.AsignacionRequest;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.dto.AsignacionResponse;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.EstadoAsignacion;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.repository.AsignacionTurnoRepository;
import com.turnapp.microservice.turnos_microservice.disponibilidad.service.IDisponibilidadService;
import com.turnapp.microservice.turnos_microservice.integration.usuario.service.IUsuarioValidationService;
import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;
import com.turnapp.microservice.turnos_microservice.turno.model.EstadoTurno;
import com.turnapp.microservice.turnos_microservice.turno.model.Turno;
import com.turnapp.microservice.turnos_microservice.turno.repository.TurnoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de asignaciones de turno.
 * Contiene la lógica crítica de validación de solapamientos y disponibilidad.
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AsignacionServiceImpl implements IAsignacionService {

  private final AsignacionTurnoRepository asignacionRepository;
  private final TurnoRepository turnoRepository;
  private final IDisponibilidadService disponibilidadService;
  private final IUsuarioValidationService usuarioValidationService;

  @Override
  @Transactional
  public AsignacionResponse crearAsignacion(AsignacionRequest request) {
    log.info("Iniciando creación de asignación para usuario: {} en fecha: {}",
        request.getUsuarioId(), request.getFecha());

    // === PASO 0: VALIDACIÓN DE USUARIO ===
    log.debug("Validando existencia de usuario: {}", request.getUsuarioId());
    if (!usuarioValidationService.existeYEstaActivo(request.getUsuarioId())) {
      log.error("Usuario no encontrado o inactivo: {}", request.getUsuarioId());
      throw new BusinessLogicException(
          "El usuario con ID " + request.getUsuarioId() + " no existe o no está activo");
    }

    log.info("Usuario validado exitosamente: {}", request.getUsuarioId());

    // === PASO 1: VALIDACIÓN DE TURNO ===
    log.debug("Buscando plantilla de turno con ID: {}", request.getTurnoId());
    Turno turno = turnoRepository.findById(request.getTurnoId())
        .orElseThrow(() -> {
          log.error("Turno no encontrado con ID: {}", request.getTurnoId());
          return new ResourceNotFoundException("Turno no encontrado con ID: " + request.getTurnoId());
        });

    // Validar que el turno esté activo
    if (turno.getEstado() != EstadoTurno.ACTIVO) {
      log.warn("Intento de asignar turno inactivo. Turno ID: {} - Estado: {}",
          turno.getId(), turno.getEstado());
      throw new BusinessLogicException(
          "El turno '" + turno.getNombre() + "' no está activo y no puede ser asignado");
    }

    // === PASO 2: VALIDACIÓN DE SOLAPAMIENTO (LÓGICA CLAVE) ===
    log.debug("Validando solapamientos para usuario: {} en fecha: {}",
        request.getUsuarioId(), request.getFecha());

    // Obtener todas las asignaciones activas del usuario en la misma fecha
    List<AsignacionTurno> asignacionesExistentes = asignacionRepository
        .findByUsuarioIdAndFechaAndEstadoNot(
            request.getUsuarioId(),
            request.getFecha(),
            EstadoAsignacion.CANCELADO);

    log.info("Se encontraron {} asignaciones existentes (no canceladas) para validación",
        asignacionesExistentes.size());

    // Validar solapamiento con cada asignación existente
    LocalTime inicioNuevo = turno.getHoraInicio();
    LocalTime finNuevo = turno.getHoraFin();

    for (AsignacionTurno existente : asignacionesExistentes) {
      LocalTime inicioExistente = existente.getTurno().getHoraInicio();
      LocalTime finExistente = existente.getTurno().getHoraFin();

      // Lógica de solapamiento: (InicioA < FinB) Y (FinA > InicioB)
      if (inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente)) {
        log.warn("Conflicto de solapamiento detectado. Usuario: {}, Turno nuevo: {} ({}-{}), " +
            "Turno existente: {} ({}-{})",
            request.getUsuarioId(), turno.getNombre(), inicioNuevo, finNuevo,
            existente.getTurno().getNombre(), inicioExistente, finExistente);

        throw new BusinessLogicException(
            String.format("Solapamiento de turno detectado. El usuario ya tiene asignado: %s (%s - %s)",
                existente.getTurno().getNombre(), inicioExistente, finExistente));
      }
    }

    log.info("Validación de solapamiento completada exitosamente");

    // === PASO 3: VALIDACIÓN DE DISPONIBILIDAD PREFERENCIAL (OPCIONAL) ===
    // Esta validación es opcional y puede ser solo una advertencia
    try {
      boolean disponible = disponibilidadService.validarDisponibilidad(
          request.getUsuarioId(),
          request.getFecha().getDayOfWeek(),
          inicioNuevo,
          finNuevo);

      if (!disponible) {
        log.warn("El turno asignado está fuera de la disponibilidad preferencial del usuario: {}",
            request.getUsuarioId());
        // Puedes decidir si esto debe ser un error o solo una advertencia
        // throw new BusinessLogicException("El turno está fuera de la disponibilidad
        // preferencial del usuario");
      } else {
        log.info("El turno coincide con la disponibilidad preferencial del usuario");
      }
    } catch (Exception e) {
      log.debug("No se pudo validar disponibilidad preferencial (puede no estar configurada): {}",
          e.getMessage());
    }

    // === PASO 4: CREACIÓN DE LA ASIGNACIÓN ===
    log.debug("Todas las validaciones pasaron. Creando entidad AsignacionTurno...");

    AsignacionTurno nuevaAsignacion = AsignacionTurno.builder()
        .usuarioId(request.getUsuarioId())
        .fecha(request.getFecha())
        .turno(turno)
        .estado(EstadoAsignacion.ASIGNADO)
        .observaciones(request.getObservaciones())
        .build();

    AsignacionTurno asignacionGuardada = asignacionRepository.save(nuevaAsignacion);
    log.info("Asignación creada exitosamente con ID: {}", asignacionGuardada.getId());

    return mapearAAsignacionResponse(asignacionGuardada);
  }

  @Override
  @Transactional
  public void cancelarAsignacion(Long id, String motivo) {
    log.info("Iniciando cancelación de asignación ID: {}", id);

    AsignacionTurno asignacion = asignacionRepository.findById(id)
        .orElseThrow(() -> {
          log.error("No se encontró la asignación a cancelar. ID: {}", id);
          return new ResourceNotFoundException("Asignación no encontrada con ID: " + id);
        });

    // Verificar que no esté ya cancelada
    if (asignacion.getEstado() == EstadoAsignacion.CANCELADO) {
      log.warn("La asignación ID: {} ya está cancelada", id);
      throw new BusinessLogicException("La asignación ya está cancelada");
    }

    // Cambiar estado a cancelado (soft delete - buena práctica)
    asignacion.setEstado(EstadoAsignacion.CANCELADO);
    asignacion.setObservaciones(
        (asignacion.getObservaciones() != null ? asignacion.getObservaciones() + " | " : "") +
            "CANCELADO: " + (motivo != null ? motivo : "Sin motivo especificado"));

    asignacionRepository.save(asignacion);
    log.info("Asignación ID: {} marcada como CANCELADA", id);
  }

  @Override
  @Transactional
  public void completarAsignacion(Long id) {
    log.info("Marcando asignación ID: {} como completada", id);

    AsignacionTurno asignacion = asignacionRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Asignación no encontrada con ID: {}", id);
          return new ResourceNotFoundException("Asignación no encontrada con ID: " + id);
        });

    asignacion.setEstado(EstadoAsignacion.COMPLETADO);
    asignacionRepository.save(asignacion);

    log.info("Asignación ID: {} marcada como COMPLETADA", id);
  }

  @Override
  @Transactional(readOnly = true)
  public AsignacionResponse obtenerAsignacionPorId(Long id) {
    log.debug("Buscando asignación con ID: {}", id);

    AsignacionTurno asignacion = asignacionRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Asignación no encontrada con ID: {}", id);
          return new ResourceNotFoundException("Asignación no encontrada con ID: " + id);
        });

    return mapearAAsignacionResponse(asignacion);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AsignacionResponse> obtenerAsignacionesPorUsuario(String usuarioId) {
    log.info("Buscando historial de asignaciones para usuario ID: {}", usuarioId);

    List<AsignacionTurno> asignaciones = asignacionRepository
        .findByUsuarioIdWithTurno(usuarioId);

    log.info("Se encontraron {} asignaciones para el usuario ID: {}",
        asignaciones.size(), usuarioId);

    return asignaciones.stream()
        .map(this::mapearAAsignacionResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AsignacionResponse> obtenerAsignacionesPorFecha(LocalDate fecha) {
    log.info("Buscando asignaciones para la fecha: {}", fecha);

    List<AsignacionTurno> asignaciones = asignacionRepository.findByFecha(fecha);

    log.info("Se encontraron {} asignaciones para la fecha: {}", asignaciones.size(), fecha);

    return asignaciones.stream()
        .map(this::mapearAAsignacionResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AsignacionResponse> obtenerAsignacionesPorUsuarioYRangoFechas(
      String usuarioId, LocalDate fechaInicio, LocalDate fechaFin) {

    log.info("Buscando asignaciones para usuario: {} entre {} y {}",
        usuarioId, fechaInicio, fechaFin);

    List<AsignacionTurno> asignaciones = asignacionRepository
        .findByUsuarioIdAndFechaBetween(usuarioId, fechaInicio, fechaFin);

    log.info("Se encontraron {} asignaciones", asignaciones.size());

    return asignaciones.stream()
        .map(this::mapearAAsignacionResponse)
        .collect(Collectors.toList());
  }

  /**
   * Convierte una entidad AsignacionTurno a AsignacionResponse DTO.
   */
  private AsignacionResponse mapearAAsignacionResponse(AsignacionTurno asignacion) {
    return AsignacionResponse.builder()
        .id(asignacion.getId())
        .usuarioId(asignacion.getUsuarioId())
        .turnoId(asignacion.getTurno().getId())
        .nombreTurno(asignacion.getTurno().getNombre())
        .fecha(asignacion.getFecha())
        .horaInicio(asignacion.getTurno().getHoraInicio())
        .horaFin(asignacion.getTurno().getHoraFin())
        .estado(asignacion.getEstado())
        .observaciones(asignacion.getObservaciones())
        .creadoEn(asignacion.getCreadoEn())
        .actualizadoEn(asignacion.getActualizadoEn())
        .build();
  }
}
