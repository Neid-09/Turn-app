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

/**
 * Entidad que representa una línea de detalle de un horario.
 * 
 * Cada detalle es una asignación planificada de un turno a un usuario en una fecha específica.
 * 
 * FLUJO DE ESTADOS:
 * 
 * 1. PLANIFICADO: 
 *    - Detalle creado en estado BORRADOR del horario
 *    - No existe asignación en turnos-microservice
 *    - asignacionId = null
 * 
 * 2. CONFIRMADO:
 *    - Horario publicado → asignación creada en turnos-microservice
 *    - asignacionId contiene el ID de la asignación en turnos
 *    - Sincronización completada
 * 
 * 3. MODIFICADO:
 *    - Asignación modificada después de publicación
 *    - Mantiene referencia a asignacionId
 * 
 * 4. CANCELADO:
 *    - Asignación cancelada
 * 
 * ESTRATEGIA DE SINCRONIZACIÓN:
 * - Fase 1 (actual): Síncrona
 *   - Al publicar horario, crea asignaciones una por una vía Feign
 *   - Guarda asignacionId en cada detalle
 * 
 * - Fase 2 (escalabilidad futura):
 *   - Asíncrona con eventos (Kafka/RabbitMQ)
 *   - Publicar evento "HorarioPublicado"
 *   - Turnos-microservice consume evento y crea asignaciones
 *   - Horarios-microservice escucha eventos de confirmación
 * 
 * @author TurnApp Team
 */
@Entity
@Table(
    name = "horarios_detalles",
    indexes = {
        @Index(name = "idx_detalle_horario_id", columnList = "horario_id"),
        @Index(name = "idx_detalle_usuario_fecha", columnList = "usuario_id, fecha"),
        @Index(name = "idx_detalle_asignacion", columnList = "asignacion_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Referencia al horario padre.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;
    
    /**
     * ID del usuario asignado (UUID de Keycloak).
     */
    @Column(nullable = false, name = "usuario_id", length = 36)
    private String usuarioId;
    
    /**
     * Fecha específica de la asignación.
     */
    @Column(nullable = false)
    private LocalDate fecha;
    
    /**
     * ID del turno (plantilla) a asignar.
     * Referencia a un Turno en el microservicio de turnos.
     */
    @Column(nullable = false, name = "turno_id")
    private Long turnoId;
    
    /**
     * Nombre del turno (cache para consultas rápidas).
     * Se llena al crear el detalle consultando turnos-microservice.
     */
    @Column(length = 100)
    private String nombreTurno;
    
    /**
     * ID de la asignación en el microservicio de turnos.
     * 
     * null: Asignación aún no creada en turnos (estado PLANIFICADO)
     * != null: Asignación creada exitosamente (estado CONFIRMADO)
     * 
     * Este campo es CRÍTICO para la sincronización entre microservicios.
     */
    @Column(name = "asignacion_id")
    private Long asignacionId;
    
    /**
     * Estado del detalle (PLANIFICADO, CONFIRMADO, MODIFICADO, CANCELADO).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoDetalle estado = EstadoDetalle.PLANIFICADO;
    
    /**
     * Observaciones o notas sobre esta asignación específica.
     */
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
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
     * Timestamp de confirmación (cuando se creó la asignación en turnos).
     */
    @Column
    private LocalDateTime confirmadoEn;
    
    /**
     * Verifica si el detalle está sincronizado con turnos-microservice.
     * 
     * @return true si tiene asignacionId (confirmado)
     */
    public boolean estaSincronizado() {
        return asignacionId != null;
    }
    
    /**
     * Marca el detalle como confirmado después de publicar exitosamente.
     * 
     * @param asignacionId ID de la asignación creada en turnos
     */
    public void marcarComoConfirmado(Long asignacionId) {
        this.asignacionId = asignacionId;
        this.estado = EstadoDetalle.CONFIRMADO;
        this.confirmadoEn = LocalDateTime.now();
    }
    
    /**
     * Verifica si el detalle puede ser modificado.
     * 
     * @return true si está en estado PLANIFICADO o CONFIRMADO
     */
    public boolean esModificable() {
        return estado == EstadoDetalle.PLANIFICADO || estado == EstadoDetalle.CONFIRMADO;
    }
}
