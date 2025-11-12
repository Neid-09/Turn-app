package com.turnapp.microservice.turnos_microservice.shared.exception;

/**
 * Excepción lanzada cuando un microservicio externo no está disponible
 * o no responde correctamente.
 * 
 * Se utiliza para manejar errores de comunicación entre microservicios,
 * como timeouts, errores de conexión, o servicios caídos.
 * 
 * @author TurnApp Team
 */
public class MicroserviceUnavailableException extends RuntimeException {
    
    private final String microserviceName;
    private final String operation;
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje descriptivo del error
     */
    public MicroserviceUnavailableException(String message) {
        super(message);
        this.microserviceName = "unknown";
        this.operation = "unknown";
    }
    
    /**
     * Constructor con mensaje y causa raíz.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz del error
     */
    public MicroserviceUnavailableException(String message, Throwable cause) {
        super(message, cause);
        this.microserviceName = "unknown";
        this.operation = "unknown";
    }
    
    /**
     * Constructor completo con detalles del microservicio.
     * 
     * @param microserviceName Nombre del microservicio que falló
     * @param operation Operación que se intentaba realizar
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz del error
     */
    public MicroserviceUnavailableException(String microserviceName, String operation, 
                                           String message, Throwable cause) {
        super(String.format("Error al comunicarse con %s durante %s: %s", 
                          microserviceName, operation, message), cause);
        this.microserviceName = microserviceName;
        this.operation = operation;
    }
    
    /**
     * Constructor completo sin causa raíz.
     * 
     * @param microserviceName Nombre del microservicio que falló
     * @param operation Operación que se intentaba realizar
     * @param message Mensaje descriptivo del error
     */
    public MicroserviceUnavailableException(String microserviceName, String operation, String message) {
        super(String.format("Error al comunicarse con %s durante %s: %s", 
                          microserviceName, operation, message));
        this.microserviceName = microserviceName;
        this.operation = operation;
    }
    
    public String getMicroserviceName() {
        return microserviceName;
    }
    
    public String getOperation() {
        return operation;
    }
}
