package com.turnapp.microservice.turnos_microservice.reemplazoTurno.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.turnapp.microservice.turnos_microservice.asignacionTurno.model.AsignacionTurno;

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
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registra la solicitud y aprobación de un reemplazo para una AsignacionTurno específica.
 * Vincula al usuario original con el usuario reemplazante.
 */
@Entity
@Table(name = "reemplazos_turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReemplazoTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reemplazo")
    private Long id;

    /** ID del usuario (externo) que tomará el turno. */
    @Column(name = "id_usuario_reemplazante", nullable = false)
    private String reemplazanteId;
    
    /** ID del administrador o supervisor que aprueba el cambio. (Referencia externa) */
    @Column(name = "aprobado_por")
    private Long aprobadorId;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "fecha_reemplazo", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReemplazo estado;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    // --- Relaciones ---

    /** Asignación de turno original que está siendo reemplazada. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignacion_original", nullable = false)
    private AsignacionTurno asignacionOriginal;
}