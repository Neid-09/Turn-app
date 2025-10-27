package com.turnapp.microservice.turnos_microservice.descanso.service;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.repository.AsignacionTurnoRepository;
import com.turnapp.microservice.turnos_microservice.descanso.dto.DescansoRequest;
import com.turnapp.microservice.turnos_microservice.descanso.dto.DescansoResponse;
import com.turnapp.microservice.turnos_microservice.descanso.model.Descanso;
import com.turnapp.microservice.turnos_microservice.descanso.repository.DescansoRepository;
import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de descansos.
 * Maneja el registro de pausas durante los turnos de trabajo.
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class DescansoServiceImpl implements IDescansoService {

  private final DescansoRepository descansoRepository;
  private final AsignacionTurnoRepository asignacionRepository;

  @Override
  @Transactional
  public DescansoResponse registrarDescanso(DescansoRequest request) {
    log.info("Registrando descanso para asignación ID: {}", request.getAsignacionId());

    // Validar que la asignación exista
    AsignacionTurno asignacion = asignacionRepository.findById(request.getAsignacionId())
        .orElseThrow(() -> {
          log.error("Asignación no encontrada con ID: {}", request.getAsignacionId());
          return new ResourceNotFoundException(
              "Asignación no encontrada con ID: " + request.getAsignacionId());
        });

    // Validar horarios del descanso
    if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
      throw new BusinessLogicException(
          "La hora de inicio del descanso debe ser anterior a la hora de fin");
    }

    // Validar que el descanso esté dentro del horario del turno
    LocalTime inicioTurno = asignacion.getTurno().getHoraInicio();
    LocalTime finTurno = asignacion.getTurno().getHoraFin();

    if (request.getHoraInicio().isBefore(inicioTurno) ||
        request.getHoraFin().isAfter(finTurno)) {
      log.warn("Descanso fuera del horario del turno. Turno: {}-{}, Descanso: {}-{}",
          inicioTurno, finTurno, request.getHoraInicio(), request.getHoraFin());
      throw new BusinessLogicException(
          String.format("El descanso debe estar dentro del horario del turno (%s - %s)",
              inicioTurno, finTurno));
    }

    Descanso descanso = Descanso.builder()
        .asignacion(asignacion)
        .horaInicio(request.getHoraInicio())
        .horaFin(request.getHoraFin())
        .tipo(request.getTipo())
        .observaciones(request.getObservaciones())
        .build();

    Descanso descansoGuardado = descansoRepository.save(descanso);
    log.info("Descanso registrado con ID: {}", descansoGuardado.getId());

    return mapearADescansoResponse(descansoGuardado);
  }

  @Override
  @Transactional(readOnly = true)
  public DescansoResponse obtenerDescansoPorId(Long id) {
    log.debug("Buscando descanso con ID: {}", id);

    Descanso descanso = descansoRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Descanso no encontrado con ID: {}", id);
          return new ResourceNotFoundException("Descanso no encontrado con ID: " + id);
        });

    return mapearADescansoResponse(descanso);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DescansoResponse> obtenerDescansosPorAsignacion(Long asignacionId) {
    log.info("Obteniendo descansos para asignación ID: {}", asignacionId);

    // Validar que la asignación exista
    if (!asignacionRepository.existsById(asignacionId)) {
      log.error("Asignación no encontrada con ID: {}", asignacionId);
      throw new ResourceNotFoundException("Asignación no encontrada con ID: " + asignacionId);
    }

    List<Descanso> descansos = descansoRepository.findByAsignacionId(asignacionId);
    log.info("Se encontraron {} descansos", descansos.size());

    return descansos.stream()
        .map(this::mapearADescansoResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void eliminarDescanso(Long id) {
    log.info("Eliminando descanso ID: {}", id);

    if (!descansoRepository.existsById(id)) {
      log.error("Descanso no encontrado con ID: {}", id);
      throw new ResourceNotFoundException("Descanso no encontrado con ID: " + id);
    }

    descansoRepository.deleteById(id);
    log.info("Descanso eliminado con ID: {}", id);
  }

  /**
   * Convierte una entidad Descanso a DescansoResponse DTO.
   */
  private DescansoResponse mapearADescansoResponse(Descanso descanso) {
    return DescansoResponse.builder()
        .id(descanso.getId())
        .asignacionId(descanso.getAsignacion().getId())
        .horaInicio(descanso.getHoraInicio())
        .horaFin(descanso.getHoraFin())
        .tipo(descanso.getTipo())
        .observaciones(descanso.getObservaciones())
        .creadoEn(descanso.getCreadoEn())
        .build();
  }
}
