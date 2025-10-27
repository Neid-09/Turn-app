package com.turnapp.microservice.turnos_microservice.turno.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidad que define una plantilla de turno.
 * Representa las características de un turno genérico (ej. "Turno Mañana")
 * sin estar asociado a un usuario o fecha específica.
 */
@Entity
@Table(name = "turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class Turno {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_turno")
  private Long id;

  /** Nombre descriptivo del turno (ej. "Turno Mañana", "Turno Noche") */
  @Column(name = "nombre_turno", nullable = false, length = 100)
  private String nombre;

  @Column(name = "hora_inicio", nullable = false)
  private LocalTime horaInicio;

  @Column(name = "hora_fin", nullable = false)
  private LocalTime horaFin;

  /**
   * Duración total calculada en minutos. (Opcional, puede calcularse al vuelo)
   */
  @Column(name = "duracion_total")
  private Integer duracionTotal;

  /** Indica si esta plantilla de turno está activa para ser usada. */
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private EstadoTurno estado;

  @CreationTimestamp // Se asigna automáticamente al crear
  @Column(name = "creado_en", updatable = false)
  private LocalDateTime creadoEn;

  @UpdateTimestamp // Se actualiza automáticamente al modificar
  @Column(name = "actualizado_en")
  private LocalDateTime actualizadoEn;

  // --- Relaciones ---

  /** Asignaciones concretas que se han hecho usando esta plantilla de turno. */
  @OneToMany(mappedBy = "turno", fetch = FetchType.LAZY)
  private Set<AsignacionTurno> asignaciones;
}