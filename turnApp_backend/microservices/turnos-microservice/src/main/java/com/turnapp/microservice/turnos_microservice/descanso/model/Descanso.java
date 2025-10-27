package com.turnapp.microservice.turnos_microservice.descanso.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Modela una pausa o descanso (ej. almuerzo) dentro de una 
 * AsignacionTurno específica.
 */
@Entity
@Table(name = "descansos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descanso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descanso")
    private Long id;

    @Column(name = "hora_inicio_descanso", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin_descanso", nullable = false)
    private LocalTime horaFin;

    /** Tipo de descanso (ej. "Almuerzo", "Pausa Activa", "Café"). */
    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    // --- Relaciones ---

    /** Asignación de turno a la cual pertenece este descanso. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignacion", nullable = false)
    private AsignacionTurno asignacion;
}
