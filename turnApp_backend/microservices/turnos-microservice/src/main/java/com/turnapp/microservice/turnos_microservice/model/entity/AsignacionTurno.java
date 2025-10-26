package com.turnapp.microservice.turnos_microservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.turnapp.microservice.turnos_microservice.model.enums.EstadoAsignacion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidad que vincula un Turno (plantilla) con un Usuario específico
 * en una Fecha específica. Esta es la instancia real de un turno de trabajo.
 */
@Entity
@Table(name = "asignaciones_turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Long id;

    /** ID del usuario (del microservicio de usuarios) al que se asigna este turno. */
    @Column(name = "id_usuario", nullable = false)
    private String usuarioId; // Es uuid en el microservicio de usuarios

    /** Día específico en que este turno debe realizarse. */
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fecha;

    /** Estado actual de la asignación (ej. Asignado, Completado, Cancelado). */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAsignacion estado;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    // --- Relaciones ---

    /** El turno (plantilla) que se está asignando. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", nullable = false)
    private Turno turno;
    
    /** Descansos registrados dentro de esta asignación de turno. */
    @OneToMany(mappedBy = "asignacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Descanso> descansos;

    /** Registros de reemplazo asociados a esta asignación específica. */
    @OneToMany(mappedBy = "asignacionOriginal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ReemplazoTurno> reemplazos;
}
