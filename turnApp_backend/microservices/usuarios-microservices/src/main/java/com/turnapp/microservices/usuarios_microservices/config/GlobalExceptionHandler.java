package com.turnapp.microservices.usuarios_microservices.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.turnapp.microservices.usuarios_microservices.usuario.dto.ErrorResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.KeycloakOperationException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.SincronizacionException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.UsuarioDuplicadoException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.UsuarioNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de excepciones para el microservicio
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Maneja errores de validación de datos (Bean Validation)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex,
      WebRequest request) {
    
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Error");
    response.put("message", "Error en la validación de datos");
    response.put("errors", errors);
    response.put("path", request.getDescription(false).replace("uri=", ""));

    log.warn("Error de validación: {}", errors);
    
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Maneja errores de acceso denegado (permisos insuficientes)
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
      AccessDeniedException ex,
      WebRequest request) {
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.FORBIDDEN.value())
        .error("Access Denied")
        .message("No tienes permisos suficientes para realizar esta acción")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.warn("Acceso denegado: {}", ex.getMessage());
    
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  /**
   * Maneja excepciones genéricas no controladas
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex,
      WebRequest request) {
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("Ha ocurrido un error inesperado")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.error("Error no controlado: ", ex);
    
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja excepciones de argumentos ilegales
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      WebRequest request) {
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.warn("Argumento ilegal: {}", ex.getMessage());
    
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Maneja excepciones cuando no se encuentra un usuario
   */
  @ExceptionHandler(UsuarioNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsuarioNotFoundException(
      UsuarioNotFoundException ex,
      WebRequest request) {
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.warn("Usuario no encontrado: {}", ex.getMessage());
    
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * Maneja excepciones de usuarios duplicados
   */
  @ExceptionHandler(UsuarioDuplicadoException.class)
  public ResponseEntity<ErrorResponse> handleUsuarioDuplicadoException(
      UsuarioDuplicadoException ex,
      WebRequest request) {
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error("Conflict")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.warn("Usuario duplicado: {}", ex.getMessage());
    
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  /**
   * Maneja excepciones de operaciones de Keycloak
   */
  @ExceptionHandler(KeycloakOperationException.class)
  public ResponseEntity<ErrorResponse> handleKeycloakOperationException(
      KeycloakOperationException ex,
      WebRequest request) {
    
    HttpStatus status = ex.getStatusCode() >= 500 
        ? HttpStatus.INTERNAL_SERVER_ERROR 
        : HttpStatus.BAD_REQUEST;
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error("Keycloak Operation Error")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.error("Error en operación de Keycloak (status: {}): {}", ex.getStatusCode(), ex.getMessage());
    
    return new ResponseEntity<>(errorResponse, status);
  }

  /**
   * Maneja excepciones de sincronización entre Keycloak y BD
   */
  @ExceptionHandler(SincronizacionException.class)
  public ResponseEntity<ErrorResponse> handleSincronizacionException(
      SincronizacionException ex,
      WebRequest request) {
    
    // Determinar el status HTTP según el tipo de inconsistencia
    HttpStatus status = HttpStatus.CONFLICT; // 409
    String errorType = "Synchronization Error";
    
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(errorType)
        .message(ex.getMessage() + " [Tipo: " + ex.getTipo() + "]")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.error("Error de sincronización Keycloak-BD: {} - Tipo: {}", 
        ex.getMessage(), ex.getTipo());
    
    return new ResponseEntity<>(errorResponse, status);
  }
}
