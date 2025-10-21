package com.turnapp.microservices.usuarios_microservices.usuario.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_id", unique = true, nullable = false, length = 36)
    private String keycloakId;

    @Column(name = "codigo_empleado", unique = true, nullable = false, length = 20)
    private String codigoEmpleado; // ID interno de la empresa

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo; // Ej: "Cajero", "Jefe de Almacén", "Supervisor de Ventas"
    
    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_app", nullable = false)
    private RolApp rolApp; // El rol que define sus permisos en la app

    @Column(name = "numero_identificacion", unique = true, nullable = false, length = 20)
    private String numeroIdentificacion;

    @Column(length = 15)
    private String telefono;
}
