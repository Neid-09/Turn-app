package com.turnapp.microservices.usuarios_microservices.usuario.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turnapp.microservices.usuarios_microservices.usuario.UsuarioRepository;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.ActualizarUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.CambiarPasswordReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.ReporteSincronizacionResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioListResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.KeycloakOperationException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.SincronizacionException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.SincronizacionException.TipoInconsistencia;

import jakarta.ws.rs.ClientErrorException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.UsuarioDuplicadoException;
import com.turnapp.microservices.usuarios_microservices.usuario.exception.UsuarioNotFoundException;
import com.turnapp.microservices.usuarios_microservices.usuario.model.Usuario;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

  private final Keycloak keycloak;
  private final UsuarioRepository usuarioRepository;

  @Value("${keycloak.admin-client.realm}")
  private String realm;

  @Override
  @Transactional
  public UsuarioResponse registrarUsuario(RegistroUsuarioReq request) {
    log.info("Registrando nuevo usuario con email: {}", request.email());

    // Verificar si ya existe un usuario con el mismo email o código de empleado
    if (usuarioRepository.existsByCodigoEmpleado(request.codigoEmpleado())) {
      throw new UsuarioDuplicadoException(
          "Ya existe un usuario con el código de empleado: " + request.codigoEmpleado());
    }

    if (usuarioRepository.existsByNumeroIdentificacion(request.numeroIdentificacion())) {
      throw new UsuarioDuplicadoException(
          "Ya existe un usuario con el número de identificación: " + request.numeroIdentificacion());
    }

    // --- 1. Crear la identidad en Keycloak ---
    UserRepresentation userRepresentation = buildUserRepresentation(request);
    UsersResource usersResource = keycloak.realm(realm).users();
    
    Response response = usersResource.create(userRepresentation);

    try {
      if (response.getStatus() == 409) {
        throw new UsuarioDuplicadoException(
            "Ya existe un usuario en Keycloak con el email: " + request.email());
      }
      
      if (response.getStatus() != 201) {
        throw new KeycloakOperationException(
            "Error al crear usuario en Keycloak", response.getStatus());
      }

      // --- 2. Obtener el ID de Keycloak del nuevo usuario ---
      String keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
      log.info("Usuario creado en Keycloak con ID: {}", keycloakId);

      // --- 2.1. Asignar rol en Keycloak ---
      try {
        asignarRolKeycloak(keycloakId, request.rolApp().name());
        log.info("Rol {} asignado al usuario en Keycloak", request.rolApp().name());
      } catch (Exception ex) {
        log.error("Error al asignar rol en Keycloak, eliminando usuario...", ex);
        usersResource.delete(keycloakId);
        throw new KeycloakOperationException(
            "Error al asignar rol al usuario en Keycloak", 500, ex);
      }

      // --- 3. Crear y guardar los datos laborales en nuestra BD ---
      Usuario nuevoUsuario = new Usuario();
      nuevoUsuario.setKeycloakId(keycloakId);
      nuevoUsuario.setCodigoEmpleado(request.codigoEmpleado());
      nuevoUsuario.setCargo(request.cargo());
      nuevoUsuario.setFechaContratacion(request.fechaContratacion());
      nuevoUsuario.setRolApp(request.rolApp());
      nuevoUsuario.setNumeroIdentificacion(request.numeroIdentificacion());
      nuevoUsuario.setTelefono(request.telefono());

      try {
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        log.info("Usuario guardado en BD local con ID: {}", usuarioGuardado.getId());

        return mapToUsuarioResponse(usuarioGuardado, userRepresentation);
        
      } catch (DataIntegrityViolationException ex) {
        // Si falla guardar en BD, eliminar de Keycloak (rollback manual)
        log.error("Error al guardar usuario en BD, eliminando de Keycloak...", ex);
        usersResource.delete(keycloakId);
        throw new UsuarioDuplicadoException("Error al guardar usuario: datos duplicados");
      }

    } finally {
      response.close();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public UsuarioResponse obtenerUsuarioPorId(String id) {
    log.info("Obteniendo usuario por ID: {}", id);
    
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

    // Validar sincronización
    UserRepresentation keycloakUser = obtenerUsuarioKeycloakConValidacion(usuario.getKeycloakId());
    
    return mapToUsuarioResponse(usuario, keycloakUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UsuarioResponse obtenerUsuarioPorKeycloakId(String keycloakId) {
    log.info("Obteniendo usuario por Keycloak ID: {}", keycloakId);
    
    // Validar que existe en Keycloak
    UserRepresentation keycloakUser = obtenerUsuarioKeycloakConValidacion(keycloakId);
    
    Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
        .orElseThrow(() -> new SincronizacionException(
            "Usuario existe en Keycloak pero no en la base de datos local. " +
            "Email: " + keycloakUser.getEmail() + ", Keycloak ID: " + keycloakId,
            TipoInconsistencia.EXISTE_EN_KEYCLOAK_NO_EN_BD));
    
    return mapToUsuarioResponse(usuario, keycloakUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UsuarioResponse obtenerUsuarioPorEmail(String email) {
    log.info("Buscando usuario por email: {}", email);
    
    // Buscar en Keycloak por email
    List<UserRepresentation> users = keycloak.realm(realm)
        .users()
        .search(email, true); // exact match

    if (users.isEmpty()) {
      throw new UsuarioNotFoundException("Usuario no encontrado con email: " + email);
    }

    UserRepresentation keycloakUser = users.get(0);
    
    // Validar sincronización
    Usuario usuario = usuarioRepository.findByKeycloakId(keycloakUser.getId())
        .orElseThrow(() -> new SincronizacionException(
            "Usuario existe en Keycloak pero no en la base de datos local. " +
            "Email: " + email + ", Keycloak ID: " + keycloakUser.getId() +
            ". Se requiere crear manualmente en la BD o eliminar de Keycloak.",
            TipoInconsistencia.EXISTE_EN_KEYCLOAK_NO_EN_BD));

    return mapToUsuarioResponse(usuario, keycloakUser);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UsuarioListResponse> listarUsuarios(Pageable pageable) {
    log.info("Listando usuarios con paginación: página {}, tamaño {}", 
        pageable.getPageNumber(), pageable.getPageSize());
    
    Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
    
    List<UsuarioListResponse> usuariosResponse = usuarios.getContent().stream()
        .map(this::mapToUsuarioListResponse)
        .collect(Collectors.toList());

    return new PageImpl<>(usuariosResponse, pageable, usuarios.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UsuarioListResponse> listarTodosLosUsuarios() {
    log.info("Listando todos los usuarios");
    
    return usuarioRepository.findAll().stream()
        .map(this::mapToUsuarioListResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UsuarioListResponse> buscarUsuarios(String searchTerm, Pageable pageable) {
    log.info("Buscando usuarios con término: {}", searchTerm);
    
    Page<Usuario> usuarios = usuarioRepository.findBySearchTerm(searchTerm, pageable);
    
    List<UsuarioListResponse> usuariosResponse = usuarios.getContent().stream()
        .map(this::mapToUsuarioListResponse)
        .collect(Collectors.toList());

    return new PageImpl<>(usuariosResponse, pageable, usuarios.getTotalElements());
  }

  @Override
  @Transactional
  public UsuarioResponse actualizarUsuario(String id, ActualizarUsuarioReq request) {
    log.info("Actualizando usuario con ID: {}", id);
    
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

    // Validar que el usuario existe en Keycloak
    UserRepresentation keycloakUser = obtenerUsuarioKeycloakConValidacion(usuario.getKeycloakId());
    UserResource userResource = keycloak.realm(realm).users().get(usuario.getKeycloakId());

    // Actualizar datos en Keycloak
    boolean keycloakUpdated = false;
    
    if (request.email() != null && !request.email().equals(keycloakUser.getEmail())) {
      log.info("Actualizando email de '{}' a '{}'", keycloakUser.getEmail(), request.email());
      keycloakUser.setEmail(request.email());
      // NO actualizar username si está configurado como read-only en Keycloak
      // keycloakUser.setUsername(request.email());
      keycloakUpdated = true;
    }
    
    if (request.firstName() != null) {
      log.info("Actualizando firstName a '{}'", request.firstName());
      keycloakUser.setFirstName(request.firstName());
      keycloakUpdated = true;
    }
    
    if (request.lastName() != null) {
      log.info("Actualizando lastName a '{}'", request.lastName());
      keycloakUser.setLastName(request.lastName());
      keycloakUpdated = true;
    }
    
    if (request.enabled() != null) {
      log.info("Actualizando enabled a '{}'", request.enabled());
      keycloakUser.setEnabled(request.enabled());
      keycloakUpdated = true;
    }

    if (keycloakUpdated) {
      log.info("Enviando actualización a Keycloak para usuario: {}", usuario.getKeycloakId());
      try {
        userResource.update(keycloakUser);
        log.info("Usuario actualizado en Keycloak: {}", usuario.getKeycloakId());
      } catch (ClientErrorException ex) {
        // Intentar leer el cuerpo de la respuesta para más detalles
        String responseBody = "";
        try {
          responseBody = ex.getResponse().readEntity(String.class);
          log.error("Error HTTP al actualizar usuario en Keycloak: {} - Response: {}", 
              ex.getResponse().getStatus(), responseBody);
        } catch (Exception e) {
          log.error("Error HTTP al actualizar usuario en Keycloak: {} - {}", 
              ex.getResponse().getStatus(), ex.getMessage());
        }
        
        String errorMessage = switch (ex.getResponse().getStatus()) {
          case 409 -> "El email '" + request.email() + 
                      "' ya está en uso por otro usuario en Keycloak";
          case 403 -> "El cliente no tiene permisos para actualizar usuarios en Keycloak";
          case 400 -> "Datos inválidos para actualización. " +
                      (responseBody.isEmpty() ? ex.getMessage() : "Detalles: " + responseBody);
          default -> "Error al actualizar usuario en Keycloak: " + ex.getMessage();
        };
        
        throw new KeycloakOperationException(errorMessage, ex.getResponse().getStatus(), ex);
      } catch (Exception ex) {
        log.error("Error inesperado al actualizar usuario en Keycloak: {}", ex.getMessage(), ex);
        throw new KeycloakOperationException(
            "Error inesperado al actualizar usuario en Keycloak: " + ex.getMessage(), 500, ex);
      }
    }

    // Actualizar datos en BD local
    if (request.cargo() != null) {
      usuario.setCargo(request.cargo());
    }
    
    if (request.fechaContratacion() != null) {
      usuario.setFechaContratacion(request.fechaContratacion());
    }
    
    if (request.rolApp() != null) {
      // Actualizar rol en Keycloak si cambió
      if (!request.rolApp().equals(usuario.getRolApp())) {
        try {
          // Remover rol anterior
          removerRolKeycloak(usuario.getKeycloakId(), usuario.getRolApp().name());
          // Asignar nuevo rol
          asignarRolKeycloak(usuario.getKeycloakId(), request.rolApp().name());
          log.info("Rol actualizado en Keycloak de {} a {}", 
              usuario.getRolApp(), request.rolApp());
        } catch (ClientErrorException ex) {
          log.error("Error HTTP al actualizar rol en Keycloak: {} - {}", 
              ex.getResponse().getStatus(), ex.getMessage());
          
          String errorMessage = switch (ex.getResponse().getStatus()) {
            case 403 -> "El cliente no tiene permisos para administrar roles. " +
                        "Debe asignar el rol 'manage-realm' al cliente 'usuarios-microservices-admin-client'";
            case 404 -> "El rol '" + request.rolApp().name() + "' no existe en Keycloak";
            default -> "Error al actualizar rol en Keycloak: " + ex.getMessage();
          };
          
          throw new KeycloakOperationException(errorMessage, ex.getResponse().getStatus(), ex);
        } catch (Exception ex) {
          log.error("Error inesperado al actualizar rol en Keycloak: {}", ex.getMessage(), ex);
          throw new KeycloakOperationException(
              "Error inesperado al actualizar rol en Keycloak: " + ex.getMessage(), 500, ex);
        }
      }
      usuario.setRolApp(request.rolApp());
    }
    
    if (request.telefono() != null) {
      usuario.setTelefono(request.telefono());
    }

    Usuario usuarioActualizado = usuarioRepository.save(usuario);
    log.info("Usuario actualizado en BD local: {}", usuarioActualizado.getId());

    // Obtener la representación actualizada de Keycloak
    UserRepresentation keycloakUserUpdated = userResource.toRepresentation();
    
    return mapToUsuarioResponse(usuarioActualizado, keycloakUserUpdated);
  }

  @Override
  @Transactional
  public void cambiarPassword(String id, CambiarPasswordReq request) {
    log.info("Cambiando contraseña para usuario ID: {}", id);
    
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

    // Validar que el usuario existe en Keycloak
    obtenerUsuarioKeycloakConValidacion(usuario.getKeycloakId());

    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(request.temporal() != null ? request.temporal() : false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(request.nuevaPassword());

    try {
      keycloak.realm(realm)
          .users()
          .get(usuario.getKeycloakId())
          .resetPassword(passwordCred);
      
      log.info("Contraseña actualizada exitosamente para usuario: {}", usuario.getKeycloakId());
      
    } catch (Exception ex) {
      throw new KeycloakOperationException(
          "Error al cambiar contraseña en Keycloak", 500, ex);
    }
  }

  @Override
  @Transactional
  public UsuarioResponse cambiarEstadoUsuario(String id, boolean enabled) {
    log.info("Cambiando estado del usuario ID: {} a {}", id, enabled ? "habilitado" : "deshabilitado");
    
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

    // Validar que el usuario existe en Keycloak
    obtenerUsuarioKeycloakConValidacion(usuario.getKeycloakId());

    UserResource userResource = keycloak.realm(realm).users().get(usuario.getKeycloakId());
    UserRepresentation keycloakUser = userResource.toRepresentation();
    
    keycloakUser.setEnabled(enabled);

    try {
      userResource.update(keycloakUser);
      log.info("Estado del usuario actualizado en Keycloak: {}", usuario.getKeycloakId());
      
      UserRepresentation updatedUser = userResource.toRepresentation();
      return mapToUsuarioResponse(usuario, updatedUser);
      
    } catch (Exception ex) {
      throw new KeycloakOperationException(
          "Error al cambiar estado del usuario en Keycloak", 500, ex);
    }
  }

  @Override
  @Transactional
  public void eliminarUsuario(String id) {
    log.info("Eliminando usuario ID: {}", id);
    
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

    try {
      keycloak.realm(realm).users().delete(usuario.getKeycloakId());
      log.info("Usuario eliminado de Keycloak: {}", usuario.getKeycloakId());
    } catch (NotFoundException ex) {
      log.warn("Usuario no encontrado en Keycloak, continuando con eliminación en BD: {}", 
          usuario.getKeycloakId());
    } catch (Exception ex) {
      throw new KeycloakOperationException(
          "Error al eliminar usuario de Keycloak", 500, ex);
    }

    // Eliminar de BD local
    usuarioRepository.delete(usuario);
    log.info("Usuario eliminado de BD local: {}", id);
  }

  // ============= MÉTODOS PRIVADOS AUXILIARES =============

  /**
   * Obtiene un usuario de Keycloak y valida que existe en la BD local.
   * Lanza SincronizacionException si hay inconsistencia.
   */
  private UserRepresentation obtenerUsuarioKeycloakConValidacion(String keycloakId) {
    try {
      UserRepresentation keycloakUser = keycloak.realm(realm)
          .users()
          .get(keycloakId)
          .toRepresentation();
      
      return keycloakUser;
      
    } catch (NotFoundException ex) {
      // Usuario no existe en Keycloak, verificar si existe en BD
      usuarioRepository.findByKeycloakId(keycloakId).ifPresent(usuario -> {
        log.error("¡INCONSISTENCIA! Usuario existe en BD pero no en Keycloak. " +
            "Keycloak ID: {}, Código Empleado: {}", keycloakId, usuario.getCodigoEmpleado());
        
        throw new SincronizacionException(
            "Usuario existe en la base de datos local pero fue eliminado de Keycloak. " +
            "Keycloak ID: " + keycloakId + 
            ", Código Empleado: " + usuario.getCodigoEmpleado() +
            ". Se requiere eliminar manualmente de la BD o restaurar en Keycloak.",
            TipoInconsistencia.EXISTE_EN_BD_NO_EN_KEYCLOAK);
      });
      
      // Si no existe en ninguno, es un error normal
      throw new UsuarioNotFoundException(
          "Usuario no encontrado en Keycloak con ID: " + keycloakId);
    }
  }

  /**
   * Obtiene un usuario de Keycloak sin validación de sincronización.
   * Usar solo cuando ya se ha validado la existencia previamente.
   */
  private UserRepresentation obtenerUsuarioKeycloak(String keycloakId) {
    try {
      return keycloak.realm(realm).users().get(keycloakId).toRepresentation();
    } catch (NotFoundException ex) {
      throw new UsuarioNotFoundException(
          "Usuario no encontrado en Keycloak con ID: " + keycloakId);
    }
  }

  /**
   * Asigna un rol de realm a un usuario en Keycloak.
   * 
   * @param keycloakId ID del usuario en Keycloak
   * @param rolName Nombre del rol a asignar (ADMIN, SUPERVISOR, EMPLEADO)
   */
  private void asignarRolKeycloak(String keycloakId, String rolName) {
    try {
      // Obtener el recurso del usuario
      UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
      
      // Obtener la representación del rol desde el realm
      RoleRepresentation roleRepresentation = keycloak.realm(realm)
          .roles()
          .get(rolName)
          .toRepresentation();
      
      // Asignar el rol al usuario
      userResource.roles()
          .realmLevel()
          .add(Collections.singletonList(roleRepresentation));
      
      log.debug("Rol {} asignado exitosamente al usuario {}", rolName, keycloakId);
      
    } catch (NotFoundException ex) {
      throw new KeycloakOperationException(
          "Rol no encontrado en Keycloak: " + rolName + 
          ". Asegúrate de que el rol existe en el realm.", 404, ex);
    } catch (Exception ex) {
      throw new KeycloakOperationException(
          "Error al asignar rol " + rolName + " al usuario", 500, ex);
    }
  }

  /**
   * Remueve un rol de realm de un usuario en Keycloak.
   * 
   * @param keycloakId ID del usuario en Keycloak
   * @param rolName Nombre del rol a remover (ADMIN, SUPERVISOR, EMPLEADO)
   */
  private void removerRolKeycloak(String keycloakId, String rolName) {
    try {
      // Obtener el recurso del usuario
      UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
      
      // Obtener la representación del rol desde el realm
      RoleRepresentation roleRepresentation = keycloak.realm(realm)
          .roles()
          .get(rolName)
          .toRepresentation();
      
      // Remover el rol del usuario
      userResource.roles()
          .realmLevel()
          .remove(Collections.singletonList(roleRepresentation));
      
      log.debug("Rol {} removido exitosamente del usuario {}", rolName, keycloakId);
      
    } catch (NotFoundException ex) {
      // Si el rol no existe, no es un error crítico, solo logueamos
      log.warn("Rol no encontrado al intentar remover: {}", rolName);
    } catch (Exception ex) {
      throw new KeycloakOperationException(
          "Error al remover rol " + rolName + " del usuario", 500, ex);
    }
  }

  private UserRepresentation buildUserRepresentation(RegistroUsuarioReq request) {
    // Objeto para la contraseña
    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(request.password());

    // Objeto para el usuario
    UserRepresentation user = new UserRepresentation();
    user.setUsername(request.email()); // Usamos el email como username
    user.setEmail(request.email());
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setCredentials(Collections.singletonList(passwordCred));
    user.setEnabled(true);
    user.setEmailVerified(true);

    return user;
  }

  private UsuarioResponse mapToUsuarioResponse(Usuario usuario, UserRepresentation keycloakUser) {
    return UsuarioResponse.builder()
        .id(usuario.getId())
        .keycloakId(usuario.getKeycloakId())
        .email(keycloakUser.getEmail())
        .firstName(keycloakUser.getFirstName())
        .lastName(keycloakUser.getLastName())
        .enabled(keycloakUser.isEnabled())
        .emailVerified(keycloakUser.isEmailVerified())
        .codigoEmpleado(usuario.getCodigoEmpleado())
        .cargo(usuario.getCargo())
        .fechaContratacion(usuario.getFechaContratacion())
        .rolApp(usuario.getRolApp())
        .numeroIdentificacion(usuario.getNumeroIdentificacion())
        .telefono(usuario.getTelefono())
        .build();
  }

  private UsuarioListResponse mapToUsuarioListResponse(Usuario usuario) {
    UserRepresentation keycloakUser = obtenerUsuarioKeycloak(usuario.getKeycloakId());
    
    return UsuarioListResponse.builder()
        .id(usuario.getId())
        .keycloakId(usuario.getKeycloakId())
        .email(keycloakUser.getEmail())
        .firstName(keycloakUser.getFirstName())
        .lastName(keycloakUser.getLastName())
        .codigoEmpleado(usuario.getCodigoEmpleado())
        .cargo(usuario.getCargo())
        .rolApp(usuario.getRolApp())
        .enabled(keycloakUser.isEnabled())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public ReporteSincronizacionResponse generarReporteSincronizacion() {
    log.info("Generando reporte de sincronización Keycloak <-> BD");

    // Obtener todos los usuarios de Keycloak
    List<UserRepresentation> usuariosKeycloak = keycloak.realm(realm)
        .users()
        .list();

    // Obtener todos los usuarios de BD
    List<Usuario> usuariosBD = usuarioRepository.findAll();

    // Crear sets para búsqueda rápida
    var keycloakIds = usuariosKeycloak.stream()
        .map(UserRepresentation::getId)
        .collect(Collectors.toSet());

    var bdKeycloakIds = usuariosBD.stream()
        .map(Usuario::getKeycloakId)
        .collect(Collectors.toSet());

    // Identificar huérfanos en Keycloak (existen en KC pero no en BD)
    List<ReporteSincronizacionResponse.UsuarioHuerfano> huerfanosKeycloak = usuariosKeycloak.stream()
        .filter(kc -> !bdKeycloakIds.contains(kc.getId()))
        .map(kc -> ReporteSincronizacionResponse.UsuarioHuerfano.builder()
            .keycloakId(kc.getId())
            .email(kc.getEmail())
            .nombre(kc.getFirstName())
            .apellido(kc.getLastName())
            .ubicacion("KEYCLOAK")
            .build())
        .collect(Collectors.toList());

    // Identificar huérfanos en BD (existen en BD pero no en KC)
    List<ReporteSincronizacionResponse.UsuarioHuerfano> huerfanosBD = usuariosBD.stream()
        .filter(bd -> !keycloakIds.contains(bd.getKeycloakId()))
        .map(bd -> ReporteSincronizacionResponse.UsuarioHuerfano.builder()
            .keycloakId(bd.getKeycloakId())
            .email("N/A") // No está en Keycloak, no tenemos el email
            .nombre(bd.getCodigoEmpleado()) // Usamos código de empleado como identificador
            .apellido(bd.getCargo())
            .ubicacion("BD")
            .build())
        .collect(Collectors.toList());

    int usuariosSincronizados = (int) usuariosBD.stream()
        .filter(bd -> keycloakIds.contains(bd.getKeycloakId()))
        .count();

    log.info("Reporte generado - KC: {}, BD: {}, Sincronizados: {}, Huérfanos KC: {}, Huérfanos BD: {}",
        usuariosKeycloak.size(), usuariosBD.size(), usuariosSincronizados,
        huerfanosKeycloak.size(), huerfanosBD.size());

    return ReporteSincronizacionResponse.builder()
        .totalUsuariosKeycloak(usuariosKeycloak.size())
        .totalUsuariosBD(usuariosBD.size())
        .usuariosSincronizados(usuariosSincronizados)
        .usuariosHuerfanosKeycloak(huerfanosKeycloak)
        .usuariosHuerfanosBD(huerfanosBD)
        .build();
  }

  @Override
  @Transactional
  public void limpiarUsuarioHuerfanoBD(String keycloakId) {
    log.info("Limpiando usuario huérfano de BD con Keycloak ID: {}", keycloakId);

    // Verificar que NO existe en Keycloak
    try {
      UserRepresentation keycloakUser = keycloak.realm(realm)
          .users()
          .get(keycloakId)
          .toRepresentation();
      
      // Si llegamos aquí, el usuario SÍ existe en Keycloak
      throw new KeycloakOperationException(
          "No se puede limpiar: el usuario existe en Keycloak. " +
          "Email: " + keycloakUser.getEmail(), 400);
          
    } catch (NotFoundException ex) {
      // Correcto: el usuario NO existe en Keycloak, procedemos a limpiar BD
      Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
          .orElseThrow(() -> new UsuarioNotFoundException(
              "Usuario no encontrado en BD con Keycloak ID: " + keycloakId));

      usuarioRepository.delete(usuario);
      log.info("Usuario huérfano eliminado de BD: Keycloak ID {}, Código Empleado: {}", 
          keycloakId, usuario.getCodigoEmpleado());
    }
  }
}
