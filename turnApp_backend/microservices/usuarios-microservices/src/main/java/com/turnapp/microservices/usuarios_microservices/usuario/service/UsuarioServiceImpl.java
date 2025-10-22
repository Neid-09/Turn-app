package com.turnapp.microservices.usuarios_microservices.usuario.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
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
  public UsuarioResponse obtenerUsuarioPorId(UUID id) {
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
  public UsuarioResponse actualizarUsuario(UUID id, ActualizarUsuarioReq request) {
    log.info("Actualizando usuario con ID: {}", id);
    
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

    // Validar que el usuario existe en Keycloak
    UserRepresentation keycloakUser = obtenerUsuarioKeycloakConValidacion(usuario.getKeycloakId());
    UserResource userResource = keycloak.realm(realm).users().get(usuario.getKeycloakId());

    // Actualizar datos en Keycloak
    boolean keycloakUpdated = false;
    
    if (request.email() != null && !request.email().equals(keycloakUser.getEmail())) {
      keycloakUser.setEmail(request.email());
      keycloakUser.setUsername(request.email());
      keycloakUpdated = true;
    }
    
    if (request.firstName() != null) {
      keycloakUser.setFirstName(request.firstName());
      keycloakUpdated = true;
    }
    
    if (request.lastName() != null) {
      keycloakUser.setLastName(request.lastName());
      keycloakUpdated = true;
    }
    
    if (request.enabled() != null) {
      keycloakUser.setEnabled(request.enabled());
      keycloakUpdated = true;
    }

    if (keycloakUpdated) {
      try {
        userResource.update(keycloakUser);
        log.info("Usuario actualizado en Keycloak: {}", usuario.getKeycloakId());
      } catch (Exception ex) {
        throw new KeycloakOperationException(
            "Error al actualizar usuario en Keycloak", 500, ex);
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
  public void cambiarPassword(UUID id, CambiarPasswordReq request) {
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
  public UsuarioResponse cambiarEstadoUsuario(UUID id, boolean enabled) {
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
  public void eliminarUsuario(UUID id) {
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
