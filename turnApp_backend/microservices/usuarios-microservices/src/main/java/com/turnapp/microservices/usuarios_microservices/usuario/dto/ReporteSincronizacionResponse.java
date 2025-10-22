package com.turnapp.microservices.usuarios_microservices.usuario.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para reportar el estado de sincronización entre Keycloak y BD.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteSincronizacionResponse {

  /**
   * Total de usuarios en Keycloak
   */
  private int totalUsuariosKeycloak;

  /**
   * Total de usuarios en base de datos
   */
  private int totalUsuariosBD;

  /**
   * Número de usuarios sincronizados correctamente
   */
  private int usuariosSincronizados;

  /**
   * Usuarios que existen en Keycloak pero no en BD (huérfanos de Keycloak)
   */
  private List<UsuarioHuerfano> usuariosHuerfanosKeycloak;

  /**
   * Usuarios que existen en BD pero no en Keycloak (huérfanos de BD)
   */
  private List<UsuarioHuerfano> usuariosHuerfanosBD;

  /**
   * DTO interno para representar usuarios huérfanos
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UsuarioHuerfano {
    private String keycloakId;
    private String email;
    private String nombre;
    private String apellido;
    private String ubicacion; // "KEYCLOAK" o "BD"
  }
}
