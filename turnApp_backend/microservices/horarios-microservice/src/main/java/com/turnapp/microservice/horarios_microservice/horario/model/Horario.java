package com.turnapp.microservice.horarios_microservice.horario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una plantilla de horario (semanal/mensual).
 * 
 * Un horario es una planificación de turnos para un período de tiempo específico.
 * Contiene múltiples líneas de detalle (HorarioDetalle) que definen las asignaciones
 * individuales de turnos a usuarios.
 * 
 * Ciclo de vida:
 * 1. BORRADOR: Creación y edición del horario
 * 2. PUBLICADO: Horario visible, asignaciones creadas en turnos-microservice
 * 3. ACTIVO: Horario vigente actualmente
 * 4. FINALIZADO: Período completado
 * 
 * ESTRATEGIA DE PERSISTENCIA:
 * - Guarda solo la plantilla en gt_horarios
 * - Al publicar, crea asignaciones en turnos-microservice (síncrono)
 * - Guarda referencias (asignacionId) en HorarioDetalle para consultas futuras
 * - Para reportes consolidados, consulta turnos-microservice vía Feign
 * 
 * NOTA PARA ESCALABILIDAD:
 * A futuro, considerar eventos (Kafka/RabbitMQ) para sincronización asíncrona
 * entre horarios y turnos cuando el volumen crezca significativamente.
 * 
 * @author TurnApp Team
 */
@Entity
@Table(name = "horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre descriptivo del horario.
     * Ejemplos: "Horario Diciembre 2025", "Semana del 15-21 Ene 2026"
     */
    @Column(nullable = false, length = 100)
    private String nombre;
    
    /**
     * Fecha de inicio del período cubierto por este horario.
     */
    @Column(nullable = false)
    private LocalDate fechaInicio;
    
    /**
     * Fecha de fin del período cubierto por este horario.
     */
    @Column(nullable = false)
    private LocalDate fechaFin;
    
    /**
     * Estado actual del horario (BORRADOR, PUBLICADO, ACTIVO, FINALIZADO, CANCELADO).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoHorario estado = EstadoHorario.BORRADOR;
    
    /**
     * Descripción o notas adicionales sobre el horario.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    /**
     * Usuario que creó el horario (UUID de Keycloak).
     */
    @Column(nullable = false)
    private String creadoPor;
    
    /**
     * Timestamp de creación.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;
    
    /**
     * Timestamp de última actualización.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime actualizadoEn;
    
    /**
     * Timestamp de publicación del horario.
     * Se establece cuando el horario pasa de BORRADOR a PUBLICADO.
     */
    @Column
    private LocalDateTime publicadoEn;
    
    /**
     * Líneas de detalle del horario (asignaciones planificadas).
     * Cascade ALL: Al eliminar el horario, se eliminan sus detalles.
     * orphanRemoval: Si se remueve un detalle de la lista, se elimina de BD.
     */
    @OneToMany(
        mappedBy = "horario",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<HorarioDetalle> detalles = new ArrayList<>();
    
    /**
     * Método helper para agregar un detalle al horario.
     * Mantiene la consistencia bidireccional de la relación.
     * 
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(HorarioDetalle detalle) {
        detalles.add(detalle);
        detalle.setHorario(this);
    }
    
    /**
     * Método helper para remover un detalle del horario.
     * 
     * @param detalle Detalle a remover
     */
    public void removerDetalle(HorarioDetalle detalle) {
        detalles.remove(detalle);
        detalle.setHorario(null);
    }
    
    /**
     * Valida que el período del horario sea coherente.
     * 
     * @return true si fechaInicio <= fechaFin
     */
    public boolean esPeriodoValido() {
        return fechaInicio != null && fechaFin != null 
            && !fechaInicio.isAfter(fechaFin);
    }
    
    /**
     * Verifica si el horario puede ser modificado.
     * Solo los horarios en estado BORRADOR pueden editarse libremente.
     * 
     * @return true si está en estado BORRADOR
     */
    public boolean esEditable() {
        return estado == EstadoHorario.BORRADOR;
    }
    
    /**
     * Verifica si el horario puede ser publicado.
     * 
     * @return true si está en estado BORRADOR y tiene detalles
     */
    public boolean esPublicable() {
        return estado == EstadoHorario.BORRADOR && !detalles.isEmpty();
    }
}
