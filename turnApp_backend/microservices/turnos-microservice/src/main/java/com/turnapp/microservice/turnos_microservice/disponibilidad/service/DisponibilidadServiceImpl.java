package com.turnapp.microservice.turnos_microservice.disponibilidad.service;

import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadRequest;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.model.Disponibilidad;
import com.turnapp.microservice.turnos_microservice.disponibilidad.repository.DisponibilidadRepository;
import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de disponibilidad preferencial.
 * Maneja la configuración de horarios preferenciales de los usuarios.
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class DisponibilidadServiceImpl implements IDisponibilidadService {

  private final DisponibilidadRepository disponibilidadRepository;

  @Override
  @Transactional
  public DisponibilidadResponse crearDisponibilidad(DisponibilidadRequest request) {
    log.info("Creando disponibilidad para usuario: {} - día: {}",
        request.getUsuarioId(), request.getDiaSemana());

    // Validar que no exista ya una regla para ese día
    if (disponibilidadRepository.existsByUsuarioIdAndDiaSemana(
        request.getUsuarioId(), request.getDiaSemana())) {
      log.warn("Ya existe disponibilidad para usuario: {} en día: {}",
          request.getUsuarioId(), request.getDiaSemana());
      throw new BusinessLogicException(
          "Ya existe una regla de disponibilidad para " + request.getDiaSemana());
    }

    // Validar horarios
    if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
      throw new BusinessLogicException("La hora de inicio debe ser anterior a la hora de fin");
    }

    Disponibilidad disponibilidad = Disponibilidad.builder()
        .usuarioId(request.getUsuarioId())
        .diaSemana(request.getDiaSemana())
        .horaInicio(request.getHoraInicio())
        .horaFin(request.getHoraFin())
        .activo(request.getActivo())
        .build();

    Disponibilidad disponibilidadGuardada = disponibilidadRepository.save(disponibilidad);
    log.info("Disponibilidad creada con ID: {}", disponibilidadGuardada.getId());

    return mapearADisponibilidadResponse(disponibilidadGuardada);
  }

  @Override
  @Transactional
  public DisponibilidadResponse actualizarDisponibilidad(Long id, DisponibilidadRequest request) {
    log.info("Actualizando disponibilidad ID: {}", id);

    Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Disponibilidad no encontrada con ID: {}", id);
          return new ResourceNotFoundException("Disponibilidad no encontrada con ID: " + id);
        });

    // Validar horarios
    if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
      throw new BusinessLogicException("La hora de inicio debe ser anterior a la hora de fin");
    }

    disponibilidad.setDiaSemana(request.getDiaSemana());
    disponibilidad.setHoraInicio(request.getHoraInicio());
    disponibilidad.setHoraFin(request.getHoraFin());
    disponibilidad.setActivo(request.getActivo());

    Disponibilidad disponibilidadActualizada = disponibilidadRepository.save(disponibilidad);
    log.info("Disponibilidad actualizada con ID: {}", id);

    return mapearADisponibilidadResponse(disponibilidadActualizada);
  }

  @Override
  @Transactional(readOnly = true)
  public DisponibilidadResponse obtenerDisponibilidadPorId(Long id) {
    log.debug("Buscando disponibilidad con ID: {}", id);

    Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Disponibilidad no encontrada con ID: {}", id);
          return new ResourceNotFoundException("Disponibilidad no encontrada con ID: " + id);
        });

    return mapearADisponibilidadResponse(disponibilidad);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DisponibilidadResponse> obtenerDisponibilidadesPorUsuario(String usuarioId) {
    log.info("Obteniendo disponibilidades para usuario: {}", usuarioId);

    List<Disponibilidad> disponibilidades = disponibilidadRepository
        .findByUsuarioId(usuarioId);

    log.info("Se encontraron {} disponibilidades", disponibilidades.size());

    return disponibilidades.stream()
        .map(this::mapearADisponibilidadResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<DisponibilidadResponse> obtenerDisponibilidadesActivasPorUsuario(String usuarioId) {
    log.info("Obteniendo disponibilidades activas para usuario: {}", usuarioId);

    List<Disponibilidad> disponibilidades = disponibilidadRepository
        .findByUsuarioIdAndActivo(usuarioId, true);

    log.info("Se encontraron {} disponibilidades activas", disponibilidades.size());

    return disponibilidades.stream()
        .map(this::mapearADisponibilidadResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void eliminarDisponibilidad(Long id) {
    log.info("Eliminando disponibilidad ID: {}", id);

    if (!disponibilidadRepository.existsById(id)) {
      log.error("Disponibilidad no encontrada con ID: {}", id);
      throw new ResourceNotFoundException("Disponibilidad no encontrada con ID: " + id);
    }

    disponibilidadRepository.deleteById(id);
    log.info("Disponibilidad eliminada con ID: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean validarDisponibilidad(String usuarioId, DayOfWeek diaSemana,
      LocalTime horaInicio, LocalTime horaFin) {
    log.debug("Validando disponibilidad para usuario: {} - día: {} - horario: {}-{}",
        usuarioId, diaSemana, horaInicio, horaFin);

    Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepository
        .findByUsuarioIdAndDiaSemana(usuarioId, diaSemana);

    // Si no hay configuración de disponibilidad, asumimos que está disponible
    if (disponibilidadOpt.isEmpty()) {
      log.debug("No hay disponibilidad configurada para ese día. Asumiendo disponible.");
      return true;
    }

    Disponibilidad disponibilidad = disponibilidadOpt.get();

    // Si la regla no está activa, asumimos que está disponible
    if (!disponibilidad.isActivo()) {
      log.debug("La regla de disponibilidad no está activa. Asumiendo disponible.");
      return true;
    }

    // Validar que el turno esté dentro del rango de disponibilidad
    boolean dentroDeRango = !horaInicio.isBefore(disponibilidad.getHoraInicio())
        && !horaFin.isAfter(disponibilidad.getHoraFin());

    if (dentroDeRango) {
      log.debug("El horario está dentro de la disponibilidad configurada");
    } else {
      log.debug("El horario está FUERA de la disponibilidad configurada ({}-{})",
          disponibilidad.getHoraInicio(), disponibilidad.getHoraFin());
    }

    return dentroDeRango;
  }

  /**
   * Convierte una entidad Disponibilidad a DisponibilidadResponse DTO.
   */
  private DisponibilidadResponse mapearADisponibilidadResponse(Disponibilidad disponibilidad) {
    return DisponibilidadResponse.builder()
        .id(disponibilidad.getId())
        .usuarioId(disponibilidad.getUsuarioId())
        .diaSemana(disponibilidad.getDiaSemana())
        .horaInicio(disponibilidad.getHoraInicio())
        .horaFin(disponibilidad.getHoraFin())
        .activo(disponibilidad.isActivo())
        .creadoEn(disponibilidad.getCreadoEn())
        .build();
  }
}
