package com.turnapp.microservices.usuarios_microservices.usuario.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turnapp.microservices.usuarios_microservices.usuario.constants.RolKeycloak;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.ActualizarUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.ApiResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.CambiarPasswordReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioInfoRes;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioListResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.service.IUsuarioService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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

  // ==================== CRUD ENDPOINTS ====================

  /**
   * Registra un nuevo usuario en el sistema.
   * Solo usuarios con rol ADMIN pueden ejecutar esta acción.
   * 
   * @param request Datos del usuario a registrar
   * @param jwt Token del administrador que realiza el registro
   * @return Respuesta con la información del usuario creado
   */
  @PostMapping
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> registrarNuevoUsuario(
      @Valid @RequestBody RegistroUsuarioReq request,
      @AuthenticationPrincipal Jwt jwt) {

    log.info("Solicitud de registro de usuario por admin: {} para nuevo usuario: {}",
        jwt.getClaim("email"), request.email());

    UsuarioResponse usuarioCreado = usuarioService.registrarUsuario(request);

    log.info("Usuario registrado exitosamente: {}", request.email());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success("Usuario creado exitosamente", usuarioCreado));
  }

  /**
   * Obtiene la información completa de un usuario por su ID.
   * Solo ADMIN puede ver información de otros usuarios.
   * 
   * @param id ID del usuario
   * @return Información completa del usuario
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable UUID id) {
    log.info("Solicitud de información del usuario ID: {}", id);

    UsuarioResponse usuario = usuarioService.obtenerUsuarioPorId(id);

    return ResponseEntity.ok(usuario);
  }

  /**
   * Obtiene la información completa de un usuario por su Keycloak ID.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param keycloakId ID de Keycloak del usuario
   * @return Información completa del usuario
   */
  @GetMapping("/keycloak/{keycloakId}")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<UsuarioResponse> obtenerUsuarioPorKeycloakId(
      @PathVariable String keycloakId) {
    
    log.info("Solicitud de información del usuario Keycloak ID: {}", keycloakId);

    UsuarioResponse usuario = usuarioService.obtenerUsuarioPorKeycloakId(keycloakId);

    return ResponseEntity.ok(usuario);
  }

  /**
   * Obtiene la información completa de un usuario por su email.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param email Email del usuario
   * @return Información completa del usuario
   */
  @GetMapping("/email/{email}")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<UsuarioResponse> obtenerUsuarioPorEmail(
      @PathVariable @Email String email) {
    
    log.info("Solicitud de información del usuario por email: {}", email);

    UsuarioResponse usuario = usuarioService.obtenerUsuarioPorEmail(email);

    return ResponseEntity.ok(usuario);
  }

  /**
   * Lista todos los usuarios con paginación.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param pageable Información de paginación
   * @return Página de usuarios
   */
  @GetMapping("/listado")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<Page<UsuarioListResponse>> listarUsuarios(
      @PageableDefault(size = 20, sort = "codigoEmpleado") Pageable pageable) {
    
    log.info("Solicitud de listado de usuarios (paginado)");

    Page<UsuarioListResponse> usuarios = usuarioService.listarUsuarios(pageable);

    return ResponseEntity.ok(usuarios);
  }

  /**
   * Lista todos los usuarios sin paginación.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @return Lista completa de usuarios
   */
  @GetMapping("/todos")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<List<UsuarioListResponse>> listarTodosLosUsuarios() {
    log.info("Solicitud de listado completo de usuarios");

    List<UsuarioListResponse> usuarios = usuarioService.listarTodosLosUsuarios();

    return ResponseEntity.ok(usuarios);
  }

  /**
   * Busca usuarios por término de búsqueda.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param searchTerm Término de búsqueda
   * @param pageable Información de paginación
   * @return Página de usuarios que coinciden
   */
  @GetMapping("/buscar")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<Page<UsuarioListResponse>> buscarUsuarios(
      @RequestParam String searchTerm,
      @PageableDefault(size = 20) Pageable pageable) {
    
    log.info("Búsqueda de usuarios con término: {}", searchTerm);

    Page<UsuarioListResponse> usuarios = usuarioService.buscarUsuarios(searchTerm, pageable);

    return ResponseEntity.ok(usuarios);
  }

  /**
   * Actualiza la información de un usuario.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param id ID del usuario
   * @param request Datos a actualizar
   * @return Información actualizada del usuario
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> actualizarUsuario(
      @PathVariable UUID id,
      @Valid @RequestBody ActualizarUsuarioReq request,
      @AuthenticationPrincipal Jwt jwt) {
    
    log.info("Solicitud de actualización del usuario ID: {} por admin: {}", 
        id, jwt.getClaim("email"));

    UsuarioResponse usuarioActualizado = usuarioService.actualizarUsuario(id, request);

    return ResponseEntity.ok(
        ApiResponse.success("Usuario actualizado exitosamente", usuarioActualizado));
  }

  /**
   * Cambia la contraseña de un usuario.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param id ID del usuario
   * @param request Nueva contraseña
   * @return Respuesta exitosa
   */
  @PatchMapping("/{id}/password")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> cambiarPassword(
      @PathVariable UUID id,
      @Valid @RequestBody CambiarPasswordReq request,
      @AuthenticationPrincipal Jwt jwt) {
    
    log.info("Solicitud de cambio de contraseña para usuario ID: {} por admin: {}", 
        id, jwt.getClaim("email"));

    usuarioService.cambiarPassword(id, request);

    return ResponseEntity.ok(
        ApiResponse.success("Contraseña actualizada exitosamente"));
  }

  /**
   * Habilita o deshabilita un usuario.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param id ID del usuario
   * @param enabled true para habilitar, false para deshabilitar
   * @return Información actualizada del usuario
   */
  @PatchMapping("/{id}/estado")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> cambiarEstadoUsuario(
      @PathVariable UUID id,
      @RequestParam boolean enabled,
      @AuthenticationPrincipal Jwt jwt) {
    
    log.info("Solicitud de cambio de estado para usuario ID: {} a {} por admin: {}", 
        id, enabled ? "habilitado" : "deshabilitado", jwt.getClaim("email"));

    UsuarioResponse usuario = usuarioService.cambiarEstadoUsuario(id, enabled);

    return ResponseEntity.ok(
        ApiResponse.success(
            "Estado del usuario actualizado exitosamente", usuario));
  }

  /**
   * Elimina un usuario del sistema.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param id ID del usuario
   * @return Respuesta exitosa
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> eliminarUsuario(
      @PathVariable UUID id,
      @AuthenticationPrincipal Jwt jwt) {
    
    log.info("Solicitud de eliminación del usuario ID: {} por admin: {}", 
        id, jwt.getClaim("email"));

    usuarioService.eliminarUsuario(id);

    return ResponseEntity.ok(
        ApiResponse.success("Usuario eliminado exitosamente"));
  }

  /**
   * Genera un reporte de sincronización entre Keycloak y BD.
   * Identifica usuarios huérfanos en ambos sistemas.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @return Reporte detallado de sincronización
   */
  @GetMapping("/sync/reporte")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> generarReporteSincronizacion(
      @AuthenticationPrincipal Jwt jwt) {
    
    String adminEmail = jwt.getClaim("email");
    log.info("Generando reporte de sincronización solicitado por admin: {}", adminEmail);

    var reporte = usuarioService.generarReporteSincronizacion();

    return ResponseEntity.ok(
        ApiResponse.success(
            "Reporte de sincronización generado exitosamente", reporte));
  }

  /**
   * Limpia un usuario huérfano de la base de datos.
   * Solo elimina si el usuario NO existe en Keycloak.
   * Solo ADMIN puede ejecutar esta acción.
   * 
   * @param keycloakId ID de Keycloak del usuario huérfano
   * @return Respuesta exitosa
   */
  @DeleteMapping("/sync/limpiar-bd/{keycloakId}")
  @PreAuthorize("hasRole('" + RolKeycloak.ADMIN + "')")
  public ResponseEntity<ApiResponse> limpiarUsuarioHuerfanoBD(
      @PathVariable String keycloakId,
      @AuthenticationPrincipal Jwt jwt) {
    
    String adminEmail = jwt.getClaim("email");
    log.info("Solicitud de limpieza de usuario huérfano en BD con Keycloak ID: {} por admin: {}", 
        keycloakId, adminEmail);

    usuarioService.limpiarUsuarioHuerfanoBD(keycloakId);

    return ResponseEntity.ok(
        ApiResponse.success("Usuario huérfano eliminado de BD exitosamente"));
  }
}
