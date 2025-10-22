package com.turnapp.microservices.usuarios_microservices.usuario.exception;

/**
 * Excepción lanzada cuando hay inconsistencia entre Keycloak y la base de datos local.
 * Por ejemplo, cuando un usuario existe en uno pero no en el otro.
 */
public class SincronizacionException extends RuntimeException {
    
    private final TipoInconsistencia tipo;
    
    public SincronizacionException(String message, TipoInconsistencia tipo) {
        super(message);
        this.tipo = tipo;
    }
    
    public TipoInconsistencia getTipo() {
        return tipo;
    }
    
    /**
     * Tipos de inconsistencia entre Keycloak y BD
     */
    public enum TipoInconsistencia {
        /** Usuario existe en BD pero no en Keycloak */
        EXISTE_EN_BD_NO_EN_KEYCLOAK,
        
        /** Usuario existe en Keycloak pero no en BD */
        EXISTE_EN_KEYCLOAK_NO_EN_BD,
        
        /** Datos no coinciden entre Keycloak y BD */
        DATOS_INCONSISTENTES
    }
}
