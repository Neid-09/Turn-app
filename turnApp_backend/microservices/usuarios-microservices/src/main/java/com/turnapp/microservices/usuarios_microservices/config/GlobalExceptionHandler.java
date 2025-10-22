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
}
