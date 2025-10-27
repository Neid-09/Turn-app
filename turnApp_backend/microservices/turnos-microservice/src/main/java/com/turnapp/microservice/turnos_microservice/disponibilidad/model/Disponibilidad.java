package com.turnapp.microservice.turnos_microservice.disponibilidad.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Modela la disponibilidad *preferencial* de un usuario.
 * Indica qué días y en qué horarios un usuario prefiere o está 
 * contratado para trabajar.
 */
@Entity
@Table(name = "disponibilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Long id;

    /** ID del usuario (externo) que define esta disponibilidad. */
    @Column(name = "id_usuario", nullable = false)
    private String usuarioId;

    /** Día de la semana para esta regla de disponibilidad. */
    @Enumerated(EnumType.STRING) // Guarda "MONDAY", "TUESDAY", etc.
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana; // Usamos el Enum nativo de Java

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /** Flag para activar o desactivar esta regla de disponibilidad. */
    @Column(name = "activo", nullable = false)
    private boolean activo;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;
}