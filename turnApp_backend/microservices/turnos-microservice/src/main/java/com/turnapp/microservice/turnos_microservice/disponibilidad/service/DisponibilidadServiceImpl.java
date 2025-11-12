package com.turnapp.microservice.turnos_microservice.disponibilidad.service;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.EstadoAsignacion;
import com.turnapp.microservice.turnos_microservice.asignacionTurno.repository.AsignacionTurnoRepository;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadRequest;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.DisponibilidadResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.dto.UsuarioDisponibleResponse;
import com.turnapp.microservice.turnos_microservice.disponibilidad.model.Disponibilidad;
import com.turnapp.microservice.turnos_microservice.disponibilidad.model.DiaSemana;
import com.turnapp.microservice.turnos_microservice.disponibilidad.repository.DisponibilidadRepository;
import com.turnapp.microservice.turnos_microservice.integration.usuario.client.UsuarioBasicoResponse;
import com.turnapp.microservice.turnos_microservice.integration.usuario.client.UsuarioClient;
import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
  private final AsignacionTurnoRepository asignacionRepository;
  private final UsuarioClient usuarioClient;

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
  public boolean validarDisponibilidad(String usuarioId, DiaSemana diaSemana,
      LocalTime horaInicio, LocalTime horaFin) {
    log.debug("Validando preferencia horaria para usuario: {} - día: {} - horario: {}-{}",
        usuarioId, diaSemana, horaInicio, horaFin);

    Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepository
        .findByUsuarioIdAndDiaSemana(usuarioId, diaSemana);

    // SISTEMA HÍBRIDO: Sin configuración = Disponible por defecto
    if (disponibilidadOpt.isEmpty()) {
      log.debug("✓ No hay restricciones configuradas. Empleado disponible por defecto (sistema híbrido)");
      return true;
    }

    Disponibilidad disponibilidad = disponibilidadOpt.get();

    // Si la regla no está activa, el empleado está disponible sin restricciones
    if (!disponibilidad.isActivo()) {
      log.debug("✓ La regla de preferencia está desactivada. Empleado disponible por defecto");
      return true;
    }

    // Validar que el turno esté dentro del rango de preferencia configurado
    boolean dentroDeRango = !horaInicio.isBefore(disponibilidad.getHoraInicio())
        && !horaFin.isAfter(disponibilidad.getHoraFin());

    if (dentroDeRango) {
      log.debug("✓ El horario está dentro de la preferencia configurada ({}-{})",
          disponibilidad.getHoraInicio(), disponibilidad.getHoraFin());
    } else {
      log.debug("⚠️ El horario está FUERA de la preferencia configurada ({}-{})",
          disponibilidad.getHoraInicio(), disponibilidad.getHoraFin());
    }

    return dentroDeRango;
  }

  @Override
  @Transactional(readOnly = true)
  public List<DisponibilidadResponse> obtenerDisponibilidadesPorPeriodo(
      LocalDate fechaInicio, LocalDate fechaFin) {
    
    log.info("Obteniendo disponibilidades configuradas entre {} y {}", fechaInicio, fechaFin);
    
    // Obtener todos los días de la semana del período
    List<DiaSemana> diasDelPeriodo = fechaInicio.datesUntil(fechaFin.plusDays(1))
        .map(fecha -> DiaSemana.fromDayOfWeek(fecha.getDayOfWeek()))
        .distinct()
        .collect(Collectors.toList());
    
    log.debug("Días de la semana en el período: {}", diasDelPeriodo);
    
    // Obtener todas las disponibilidades activas para esos días
    List<Disponibilidad> disponibilidades = disponibilidadRepository
        .findByDiaSemanaInAndActivo(diasDelPeriodo, true);
    
    log.info("Se encontraron {} disponibilidades configuradas en el período", 
             disponibilidades.size());
    
    return disponibilidades.stream()
        .map(this::mapearADisponibilidadResponse)
        .collect(Collectors.toList());
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

  @Override
  @Transactional(readOnly = true)
  public List<UsuarioDisponibleResponse> obtenerUsuariosDisponibles(
      LocalDate fecha,
      DiaSemana diaSemana,
      LocalTime horaInicio,
      LocalTime horaFin) {
    
    log.info("Obteniendo usuarios disponibles para {} ({}) entre {} y {}", 
             fecha, diaSemana, horaInicio, horaFin);
    
    // 1. Obtener TODOS los usuarios del microservicio de usuarios
    List<UsuarioBasicoResponse> todosLosUsuarios;
    try {
      todosLosUsuarios = usuarioClient.obtenerTodosLosUsuarios();
      log.debug("Se obtuvieron {} usuarios del microservicio", todosLosUsuarios.size());
      
    } catch (feign.FeignException.NotFound e) {
      // Error 404 - El endpoint /todos tiene problemas internos
      log.error("Error 404 al obtener usuarios - Endpoint /todos tiene problemas: {}", e.getMessage());
      throw new com.turnapp.microservice.turnos_microservice.shared.exception.MicroserviceUnavailableException(
          "usuarios-microservice",
          "listar todos los usuarios",
          "El servicio de usuarios tiene un problema de configuración interno (error 404). " +
          "Esto puede ocurrir cuando hay usuarios en la base de datos que no existen en Keycloak. " +
          "Por favor, contacte al administrador del sistema.",
          e
      );
      
    } catch (feign.FeignException e) {
      // Otros errores de Feign (timeout, 503, 500, etc.)
      log.error("Error de comunicación con microservicio de usuarios: Status={}, Mensaje={}", 
               e.status(), e.getMessage());
      
      String mensaje = determinarMensajeErrorFeign(e);
      throw new com.turnapp.microservice.turnos_microservice.shared.exception.MicroserviceUnavailableException(
          "usuarios-microservice",
          "obtener lista de usuarios",
          mensaje,
          e
      );
      
    } catch (Exception e) {
      // Cualquier otra excepción inesperada
      log.error("Error inesperado al obtener usuarios: {}", e.getMessage(), e);
      throw new com.turnapp.microservice.turnos_microservice.shared.exception.MicroserviceUnavailableException(
          "usuarios-microservice",
          "obtener lista de usuarios",
          "Error inesperado al comunicarse con el servicio de usuarios. Por favor, intente nuevamente.",
          e
      );
    }
    
    // 2. Obtener usuarios ya asignados en la fecha especificada (excluyendo cancelados)
    List<String> usuariosYaAsignados = asignacionRepository
        .findByFecha(fecha)
        .stream()
        .filter(asignacion -> asignacion.getEstado() != EstadoAsignacion.CANCELADO)
        .map(asignacion -> asignacion.getUsuarioId())
        .distinct()
        .collect(Collectors.toList());
    
    log.debug("Usuarios ya asignados en {}: {}", fecha, usuariosYaAsignados.size());
    
    // 3. Filtrar solo usuarios habilitados, activos y SIN asignaciones en la fecha
    List<UsuarioBasicoResponse> usuariosDisponibles = todosLosUsuarios.stream()
        .filter(usuario -> usuario.getEnabled() != null && usuario.getEnabled())
        .filter(usuario -> !usuariosYaAsignados.contains(usuario.getKeycloakId()))
        .collect(Collectors.toList());
    
    log.debug("Se filtraron {} usuarios disponibles (activos y sin asignaciones)", usuariosDisponibles.size());
    
    // 4. Aplicar lógica híbrida de disponibilidad
    List<UsuarioDisponibleResponse> usuariosConDisponibilidad = usuariosDisponibles.stream()
        .map(usuario -> evaluarDisponibilidadUsuario(usuario, diaSemana, horaInicio, horaFin))
        .collect(Collectors.toList());
    
    log.info("Total de usuarios disponibles: {}", usuariosConDisponibilidad.size());
    log.info("  - Usuarios que cumplen preferencias: {}", 
             usuariosConDisponibilidad.stream().filter(UsuarioDisponibleResponse::isCumplePreferencias).count());
    log.info("  - Usuarios sin preferencias configuradas: {}", 
             usuariosConDisponibilidad.stream().filter(u -> !u.isTienePreferencias()).count());
    log.info("  - Usuarios excluidos por asignaciones: {}", usuariosYaAsignados.size());
    
    return usuariosConDisponibilidad;
  }
  
  /**
   * Determina un mensaje de error descriptivo basado en el status de FeignException.
   */
  private String determinarMensajeErrorFeign(feign.FeignException e) {
    int status = e.status();
    
    if (status == -1) {
      return "El servicio de usuarios no está disponible (timeout o sin conexión). Por favor, intente nuevamente.";
    } else if (status == 503) {
      return "El servicio de usuarios está temporalmente fuera de servicio. Por favor, intente más tarde.";
    } else if (status >= 500) {
      return "El servicio de usuarios está experimentando problemas técnicos. Por favor, contacte al administrador.";
    } else if (status == 401 || status == 403) {
      return "Error de autenticación con el servicio de usuarios. Por favor, contacte al administrador.";
    } else {
      return "Error al comunicarse con el servicio de usuarios (código " + status + "). Por favor, intente nuevamente.";
    }
  }

  /**
   * Evalúa la disponibilidad de un usuario para un día y horario específico.
   * Aplica la lógica híbrida del sistema.
   */
  private UsuarioDisponibleResponse evaluarDisponibilidadUsuario(
      UsuarioBasicoResponse usuario,
      DiaSemana diaSemana,
      LocalTime horaInicio,
      LocalTime horaFin) {
    
    String keycloakId = usuario.getKeycloakId();
    String nombreCompleto = String.format("%s %s", 
                                         usuario.getFirstName() != null ? usuario.getFirstName() : "",
                                         usuario.getLastName() != null ? usuario.getLastName() : "").trim();
    
    // Buscar si tiene disponibilidad configurada para este día
    Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepository
        .findByUsuarioIdAndDiaSemana(keycloakId, diaSemana);
    
    // CASO 1: No tiene preferencias configuradas - DISPONIBLE POR DEFECTO
    if (disponibilidadOpt.isEmpty()) {
      log.debug("Usuario {} sin preferencias configuradas - Disponible por defecto", keycloakId);
      
      return UsuarioDisponibleResponse.builder()
          .keycloakId(keycloakId)
          .nombreCompleto(nombreCompleto)
          .codigoEmpleado(usuario.getCodigoEmpleado())
          .rolApp(usuario.getRolApp())
          .tienePreferencias(false)
          .cumplePreferencias(true)
          .mensaje("Disponible (sin preferencias configuradas)")
          .build();
    }
    
    Disponibilidad disponibilidad = disponibilidadOpt.get();
    
    // CASO 2: Tiene preferencias pero están desactivadas - DISPONIBLE
    if (!disponibilidad.isActivo()) {
      log.debug("Usuario {} con preferencias desactivadas - Disponible", keycloakId);
      
      return UsuarioDisponibleResponse.builder()
          .keycloakId(keycloakId)
          .nombreCompleto(nombreCompleto)
          .codigoEmpleado(usuario.getCodigoEmpleado())
          .rolApp(usuario.getRolApp())
          .tienePreferencias(true)
          .cumplePreferencias(true)
          .mensaje("Disponible (preferencias desactivadas)")
          .build();
    }
    
    // CASO 3: Tiene preferencias activas - VALIDAR HORARIO
    boolean dentroDeRango = !horaInicio.isBefore(disponibilidad.getHoraInicio())
        && !horaFin.isAfter(disponibilidad.getHoraFin());
    
    if (dentroDeRango) {
      log.debug("Usuario {} cumple preferencias ({} - {})", 
               keycloakId, disponibilidad.getHoraInicio(), disponibilidad.getHoraFin());
      
      return UsuarioDisponibleResponse.builder()
          .keycloakId(keycloakId)
          .nombreCompleto(nombreCompleto)
          .codigoEmpleado(usuario.getCodigoEmpleado())
          .rolApp(usuario.getRolApp())
          .tienePreferencias(true)
          .cumplePreferencias(true)
          .mensaje(String.format("Disponible dentro de preferencias (%s - %s)",
                                disponibilidad.getHoraInicio(), disponibilidad.getHoraFin()))
          .build();
    } else {
      log.debug("Usuario {} FUERA de preferencias ({} - {})", 
               keycloakId, disponibilidad.getHoraInicio(), disponibilidad.getHoraFin());
      
      return UsuarioDisponibleResponse.builder()
          .keycloakId(keycloakId)
          .nombreCompleto(nombreCompleto)
          .codigoEmpleado(usuario.getCodigoEmpleado())
          .rolApp(usuario.getRolApp())
          .tienePreferencias(true)
          .cumplePreferencias(false)
          .mensaje(String.format("Disponible pero FUERA de preferencias (%s - %s)",
                                disponibilidad.getHoraInicio(), disponibilidad.getHoraFin()))
          .build();
    }
  }
}
