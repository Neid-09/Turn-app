package com.turnapp.microservices.usuarios_microservices.usuario.constants;

/**
 * Constantes para los roles definidos en Keycloak.
 * Estos deben coincidir exactamente con los roles configurados en el realm de Keycloak.
 */
public final class RolKeycloak {
  
  private RolKeycloak() {
    throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
  }

  /**
   * Rol de administrador del sistema.
   * Tiene permisos completos sobre todas las funcionalidades.
   */
  public static final String ADMIN = "ADMIN";

  /**
   * Rol de supervisor.
   * Puede supervisar y gestionar empleados.
   */
  public static final String SUPERVISOR = "SUPERVISOR";

  /**
   * Rol de empleado estándar.
   * Permisos básicos de acceso.
   */
  public static final String EMPLEADO = "EMPLEADO";
}
