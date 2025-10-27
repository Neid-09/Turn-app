package com.turnapp.microservice.turnos_microservice.reemplazoTurno.service;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.EstadoAsignacion;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.repository.AsignacionTurnoRepository;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto.ReemplazoRequest;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.dto.ReemplazoResponse;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.model.EstadoReemplazo;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.model.ReemplazoTurno;
import com.turnapp.microservice.turnos_microservice.reemplazoTurno.repository.ReemplazoTurnoRepository;
import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de reemplazos de turno.
 * Maneja las solicitudes y aprobaciones de reemplazos entre usuarios.
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ReemplazoServiceImpl implements IReemplazoService {

  private final ReemplazoTurnoRepository reemplazoRepository;
  private final AsignacionTurnoRepository asignacionRepository;

  @Override
  @Transactional
  public ReemplazoResponse solicitarReemplazo(ReemplazoRequest request) {
    log.info("Creando solicitud de reemplazo para asignación ID: {} - Reemplazante: {}",
        request.getAsignacionOriginalId(), request.getReemplazanteId());

    // Validar que la asignación original exista
    AsignacionTurno asignacionOriginal = asignacionRepository
        .findById(request.getAsignacionOriginalId())
        .orElseThrow(() -> {
          log.error("Asignación original no encontrada con ID: {}",
              request.getAsignacionOriginalId());
          return new ResourceNotFoundException(
              "Asignación no encontrada con ID: " + request.getAsignacionOriginalId());
        });

    // Validar que la asignación no esté ya cancelada o completada
    if (asignacionOriginal.getEstado() == EstadoAsignacion.CANCELADO) {
      throw new BusinessLogicException("No se puede reemplazar una asignación cancelada");
    }
    if (asignacionOriginal.getEstado() == EstadoAsignacion.COMPLETADO) {
      throw new BusinessLogicException("No se puede reemplazar una asignación completada");
    }

    // Validar que el usuario reemplazante no sea el mismo que el original
    if (asignacionOriginal.getUsuarioId().equals(request.getReemplazanteId())) {
      throw new BusinessLogicException(
          "El usuario reemplazante no puede ser el mismo que el usuario original");
    }

    // TODO: Aquí podrías validar que el reemplazante no tenga conflictos de horario
    // usando la misma lógica de solapamiento que en AsignacionServiceImpl

    ReemplazoTurno reemplazo = ReemplazoTurno.builder()
        .asignacionOriginal(asignacionOriginal)
        .reemplazanteId(request.getReemplazanteId())
        .fecha(request.getFecha())
        .motivo(request.getMotivo())
        .estado(EstadoReemplazo.PENDIENTE)
        .build();

    ReemplazoTurno reemplazoGuardado = reemplazoRepository.save(reemplazo);
    log.info("Solicitud de reemplazo creada con ID: {}", reemplazoGuardado.getId());

    return mapearAReemplazoResponse(reemplazoGuardado);
  }

  @Override
  @Transactional
  public ReemplazoResponse aprobarReemplazo(Long id, Long aprobadorId) {
    log.info("Aprobando reemplazo ID: {} por aprobador: {}", id, aprobadorId);

    ReemplazoTurno reemplazo = reemplazoRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Reemplazo no encontrado con ID: {}", id);
          return new ResourceNotFoundException("Reemplazo no encontrado con ID: " + id);
        });

    // Validar estado
    if (reemplazo.getEstado() != EstadoReemplazo.PENDIENTE) {
      throw new BusinessLogicException(
          "Solo se pueden aprobar reemplazos en estado PENDIENTE. Estado actual: "
              + reemplazo.getEstado());
    }

    // Aprobar el reemplazo
    reemplazo.setEstado(EstadoReemplazo.APROBADO);
    reemplazo.setAprobadorId(aprobadorId);

    // Actualizar la asignación original: cambiar el usuario al reemplazante
    AsignacionTurno asignacionOriginal = reemplazo.getAsignacionOriginal();
    String usuarioOriginal = asignacionOriginal.getUsuarioId();
    asignacionOriginal.setUsuarioId(reemplazo.getReemplazanteId());
    asignacionOriginal.setObservaciones(
        (asignacionOriginal.getObservaciones() != null ? asignacionOriginal.getObservaciones() + " | " : "") +
            String.format("REEMPLAZADO: Usuario original %s reemplazado por %s. Motivo: %s",
                usuarioOriginal, reemplazo.getReemplazanteId(), reemplazo.getMotivo()));

    asignacionRepository.save(asignacionOriginal);
    ReemplazoTurno reemplazoAprobado = reemplazoRepository.save(reemplazo);

    log.info("Reemplazo ID: {} aprobado exitosamente. Usuario {} -> {}",
        id, usuarioOriginal, reemplazo.getReemplazanteId());

    return mapearAReemplazoResponse(reemplazoAprobado);
  }

  @Override
  @Transactional
  public ReemplazoResponse rechazarReemplazo(Long id, Long aprobadorId, String motivo) {
    log.info("Rechazando reemplazo ID: {} por aprobador: {}", id, aprobadorId);

    ReemplazoTurno reemplazo = reemplazoRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Reemplazo no encontrado con ID: {}", id);
          return new ResourceNotFoundException("Reemplazo no encontrado con ID: " + id);
        });

    // Validar estado
    if (reemplazo.getEstado() != EstadoReemplazo.PENDIENTE) {
      throw new BusinessLogicException(
          "Solo se pueden rechazar reemplazos en estado PENDIENTE. Estado actual: "
              + reemplazo.getEstado());
    }

    reemplazo.setEstado(EstadoReemplazo.RECHAZADO);
    reemplazo.setAprobadorId(aprobadorId);
    reemplazo.setMotivo(reemplazo.getMotivo() + " | RECHAZADO: " +
        (motivo != null ? motivo : "Sin motivo especificado"));

    ReemplazoTurno reemplazoRechazado = reemplazoRepository.save(reemplazo);
    log.info("Reemplazo ID: {} rechazado", id);

    return mapearAReemplazoResponse(reemplazoRechazado);
  }

  @Override
  @Transactional(readOnly = true)
  public ReemplazoResponse obtenerReemplazoPorId(Long id) {
    log.debug("Buscando reemplazo con ID: {}", id);

    ReemplazoTurno reemplazo = reemplazoRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Reemplazo no encontrado con ID: {}", id);
          return new ResourceNotFoundException("Reemplazo no encontrado con ID: " + id);
        });

    return mapearAReemplazoResponse(reemplazo);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReemplazoResponse> obtenerReemplazosPendientes() {
    log.info("Obteniendo reemplazos pendientes de aprobación");

    List<ReemplazoTurno> reemplazos = reemplazoRepository.findPendientes();
    log.info("Se encontraron {} reemplazos pendientes", reemplazos.size());

    return reemplazos.stream()
        .map(this::mapearAReemplazoResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReemplazoResponse> obtenerReemplazosPorReemplazante(String reemplazanteId) {
    log.info("Obteniendo reemplazos para reemplazante: {}", reemplazanteId);

    List<ReemplazoTurno> reemplazos = reemplazoRepository
        .findByReemplazanteId(reemplazanteId);

    log.info("Se encontraron {} reemplazos", reemplazos.size());

    return reemplazos.stream()
        .map(this::mapearAReemplazoResponse)
        .collect(Collectors.toList());
  }

  /**
   * Convierte una entidad ReemplazoTurno a ReemplazoResponse DTO.
   */
  private ReemplazoResponse mapearAReemplazoResponse(ReemplazoTurno reemplazo) {
    return ReemplazoResponse.builder()
        .id(reemplazo.getId())
        .asignacionOriginalId(reemplazo.getAsignacionOriginal().getId())
        .reemplazanteId(reemplazo.getReemplazanteId())
        .aprobadorId(reemplazo.getAprobadorId())
        .motivo(reemplazo.getMotivo())
        .fecha(reemplazo.getFecha())
        .estado(reemplazo.getEstado())
        .creadoEn(reemplazo.getCreadoEn())
        .build();
  }
}
