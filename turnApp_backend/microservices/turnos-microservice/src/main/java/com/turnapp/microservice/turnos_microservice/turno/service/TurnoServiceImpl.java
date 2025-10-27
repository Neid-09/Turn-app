package com.turnapp.microservice.turnos_microservice.turno.service;

import com.turnapp.microservice.turnos_microservice.shared.exception.BusinessLogicException;
import com.turnapp.microservice.turnos_microservice.shared.exception.ResourceNotFoundException;
import com.turnapp.microservice.turnos_microservice.turno.dto.TurnoRequest;
import com.turnapp.microservice.turnos_microservice.turno.dto.TurnoResponse;
import com.turnapp.microservice.turnos_microservice.turno.model.EstadoTurno;
import com.turnapp.microservice.turnos_microservice.turno.model.Turno;
import com.turnapp.microservice.turnos_microservice.turno.repository.TurnoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de plantillas de turno.
 * Maneja la lógica de negocio para crear, actualizar y consultar turnos.
 * 
 * @author TurnApp Team
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class TurnoServiceImpl implements ITurnoService {
        
    private final TurnoRepository turnoRepository;
    
    @Override
    @Transactional
    public TurnoResponse crearTurno(TurnoRequest request) {
        log.info("Iniciando creación de turno con nombre: {}", request.getNombre());
        
        // Validar que no exista un turno con el mismo nombre
        if (turnoRepository.existsByNombre(request.getNombre())) {
            log.warn("Intento de crear turno duplicado con nombre: {}", request.getNombre());
            throw new BusinessLogicException("Ya existe un turno con el nombre: " + request.getNombre());
        }
        
        // Validar que la hora de inicio sea antes de la hora de fin
        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            log.warn("Horario inválido: hora inicio {} debe ser antes de hora fin {}", 
                     request.getHoraInicio(), request.getHoraFin());
            throw new BusinessLogicException("La hora de inicio debe ser anterior a la hora de fin");
        }
        
        // Calcular duración si no se proporciona
        Integer duracion = request.getDuracionTotal();
        if (duracion == null) {
            duracion = (int) Duration.between(request.getHoraInicio(), request.getHoraFin()).toMinutes();
            log.debug("Duración calculada automáticamente: {} minutos", duracion);
        }
        
        Turno turno = Turno.builder()
                .nombre(request.getNombre())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .duracionTotal(duracion)
                .estado(request.getEstado())
                .build();
        
        Turno turnoGuardado = turnoRepository.save(turno);
        log.info("Turno creado exitosamente con ID: {}", turnoGuardado.getId());
        
        return mapearATurnoResponse(turnoGuardado);
    }
    
    @Override
    @Transactional
    public TurnoResponse actualizarTurno(Long id, TurnoRequest request) {
        log.info("Actualizando turno con ID: {}", id);
        
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turno no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Turno no encontrado con ID: " + id);
                });
        
        // Validar horarios
        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            throw new BusinessLogicException("La hora de inicio debe ser anterior a la hora de fin");
        }
        
        // Actualizar campos
        turno.setNombre(request.getNombre());
        turno.setHoraInicio(request.getHoraInicio());
        turno.setHoraFin(request.getHoraFin());
        turno.setEstado(request.getEstado());
        
        // Recalcular duración
        Integer duracion = request.getDuracionTotal();
        if (duracion == null) {
            duracion = (int) Duration.between(request.getHoraInicio(), request.getHoraFin()).toMinutes();
        }
        turno.setDuracionTotal(duracion);
        
        Turno turnoActualizado = turnoRepository.save(turno);
        log.info("Turno actualizado exitosamente con ID: {}", id);
        
        return mapearATurnoResponse(turnoActualizado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TurnoResponse obtenerTurnoPorId(Long id) {
        log.debug("Buscando turno con ID: {}", id);
        
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turno no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Turno no encontrado con ID: " + id);
                });
        
        return mapearATurnoResponse(turno);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> obtenerTodosTurnos() {
        log.debug("Obteniendo todos los turnos");
        
        List<Turno> turnos = turnoRepository.findAll();
        log.info("Se encontraron {} turnos", turnos.size());
        
        return turnos.stream()
                .map(this::mapearATurnoResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> obtenerTurnosActivos() {
        log.debug("Obteniendo turnos activos");
        
        List<Turno> turnos = turnoRepository.findByEstado(EstadoTurno.ACTIVO);
        log.info("Se encontraron {} turnos activos", turnos.size());
        
        return turnos.stream()
                .map(this::mapearATurnoResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void eliminarTurno(Long id) {
        log.info("Desactivando turno con ID: {}", id);
        
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Turno no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Turno no encontrado con ID: " + id);
                });
        
        // En lugar de eliminar, desactivamos el turno (soft delete)
        turno.setEstado(EstadoTurno.INACTIVO);
        turnoRepository.save(turno);
        
        log.info("Turno desactivado exitosamente con ID: {}", id);
    }
    
    /**
     * Convierte una entidad Turno a TurnoResponse DTO.
     */
    private TurnoResponse mapearATurnoResponse(Turno turno) {
        return TurnoResponse.builder()
                .id(turno.getId())
                .nombre(turno.getNombre())
                .horaInicio(turno.getHoraInicio())
                .horaFin(turno.getHoraFin())
                .duracionTotal(turno.getDuracionTotal())
                .estado(turno.getEstado())
                .creadoEn(turno.getCreadoEn())
                .actualizadoEn(turno.getActualizadoEn())
                .build();
    }
}
