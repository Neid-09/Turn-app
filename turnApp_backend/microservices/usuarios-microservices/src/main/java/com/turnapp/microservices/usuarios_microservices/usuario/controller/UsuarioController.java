package com.turnapp.microservices.usuarios_microservices.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turnapp.microservices.usuarios_microservices.usuario.constants.RolKeycloak;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.ApiResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioInfoRes;
import com.turnapp.microservices.usuarios_microservices.usuario.service.IUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para operaciones CRUD y autenticación de usuarios.
 */
@Slf4j
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

  private final IUsuarioService usuarioService;

  /**
   * Endpoint de prueba para verificar que el servicio está funcionando.
   * Cualquier usuario autenticado puede acceder.
   * 
   * @return Mensaje de bienvenida
   */
  @GetMapping
  public ResponseEntity<ApiResponse> healthCheck() {
    log.info("Health check solicitado");
    return ResponseEntity.ok(
        ApiResponse.success("Microservicio de usuarios operativo"));
  }

  /**
   * Obtiene la información del usuario autenticado actualmente.
   * Extrae los datos del token JWT.
   * 
   * @param jwt Token JWT del usuario autenticado
   * @return Información del usuario
   */
  @GetMapping("/me")
  public ResponseEntity<UsuarioInfoRes> obtenerUsuarioActual(@AuthenticationPrincipal Jwt jwt) {
    log.info("Solicitud de información del usuario: {}", jwt.getSubject());

    UsuarioInfoRes userInfo = UsuarioInfoRes.builder()
        .id(jwt.getSubject())
        .email(jwt.getClaim("email"))
        .firstName(jwt.getClaim("given_name"))
        .lastName(jwt.getClaim("family_name"))
        .enabled(true)
        .build();

    return ResponseEntity.ok(userInfo);
  }

  /**
   * Obtiene los roles del usuario autenticado.
   * 
   * @param authentication Objeto de autenticación con los roles
   * @return Lista de roles del usuario
   */
  @GetMapping("/me/roles")
  public ResponseEntity<ApiResponse> obtenerRolesUsuario(Authentication authentication) {
    log.info("Solicitud de roles del usuario: {}", authentication.getName());

    return ResponseEntity.ok(
        ApiResponse.success(
            "Roles del usuario obtenidos exitosamente",
            authentication.getAuthorities()));
  }

  /**
   * Registra un nuevo usuario en el sistema.
   * Solo usuarios con rol ADMIN pueden ejecutar esta acción.
   * 
   * @param request Datos del usuario a registrar
   * @param jwt     Token del administrador que realiza el registro
   * @return Respuesta con el resultado del registro
   */
  @PostMapping("/registro")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> registrarNuevoUsuario(
      @Valid @RequestBody RegistroUsuarioReq request,
      @AuthenticationPrincipal Jwt jwt) {

    log.info("Solicitud de registro de usuario por admin: {} para nuevo usuario: {}",
        jwt.getClaim("email"), request.email());

    usuarioService.registrarUsuario(request);

    log.info("Usuario registrado exitosamente: {}", request.email());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success("Usuario creado exitosamente"));
  }
}
