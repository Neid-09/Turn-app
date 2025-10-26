package com.turnapp.microservices.usuarios_microservices.usuario.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.turnapp.microservices.usuarios_microservices.usuario.dto.ActualizarUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.CambiarPasswordReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.ReporteSincronizacionResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioListResponse;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.UsuarioResponse;

/**
 * Interfaz de servicio para operaciones CRUD de usuarios.
 * Gestiona tanto la información en Keycloak como en la base de datos local.
 */
public interface IUsuarioService {

  /**
   * Registra un nuevo usuario en Keycloak y en la base de datos local.
   * 
   * @param request Datos del usuario a registrar
   * @return Información completa del usuario creado
   */
  UsuarioResponse registrarUsuario(RegistroUsuarioReq request);

  /**
   * Obtiene la información completa de un usuario por su ID interno.
   * 
   * @param id ID interno del usuario en la base de datos
   * @return Información completa del usuario
   */
  UsuarioResponse obtenerUsuarioPorId(String id);

  /**
   * Obtiene la información completa de un usuario por su ID de Keycloak.
   * 
   * @param keycloakId ID del usuario en Keycloak
   * @return Información completa del usuario
   */
  UsuarioResponse obtenerUsuarioPorKeycloakId(String keycloakId);

  /**
   * Obtiene la información completa de un usuario por su email.
   * 
   * @param email Email del usuario
   * @return Información completa del usuario
   */
  UsuarioResponse obtenerUsuarioPorEmail(String email);

  /**
   * Lista todos los usuarios con paginación.
   * 
   * @param pageable Información de paginación
   * @return Página de usuarios
   */
  Page<UsuarioListResponse> listarUsuarios(Pageable pageable);

  /**
   * Lista todos los usuarios sin paginación.
   * 
   * @return Lista de todos los usuarios
   */
  List<UsuarioListResponse> listarTodosLosUsuarios();

  /**
   * Busca usuarios por nombre, apellido o email.
   * 
   * @param searchTerm Término de búsqueda
   * @param pageable Información de paginación
   * @return Página de usuarios que coinciden con la búsqueda
   */
  Page<UsuarioListResponse> buscarUsuarios(String searchTerm, Pageable pageable);

  /**
   * Actualiza la información de un usuario existente.
   * Solo se actualizan los campos proporcionados (no nulos).
   * 
   * @param id ID interno del usuario
   * @param request Datos a actualizar
   * @return Información actualizada del usuario
   */
  UsuarioResponse actualizarUsuario(String id, ActualizarUsuarioReq request);

  /**
   * Cambia la contraseña de un usuario.
   * 
   * @param id ID interno del usuario
   * @param request Nueva contraseña
   */
  void cambiarPassword(String id, CambiarPasswordReq request);

  /**
   * Habilita o deshabilita un usuario en Keycloak.
   * 
   * @param id ID interno del usuario
   * @param enabled true para habilitar, false para deshabilitar
   * @return Información actualizada del usuario
   */
  UsuarioResponse cambiarEstadoUsuario(String id, boolean enabled);

  /**
   * Elimina un usuario de Keycloak y de la base de datos local.
   * 
   * @param id ID interno del usuario
   */
  void eliminarUsuario(String id);

  /**
   * Genera un reporte de sincronización entre Keycloak y BD.
   * Identifica usuarios huérfanos en ambos sistemas.
   * 
   * @return Reporte detallado de sincronización
   */
  ReporteSincronizacionResponse generarReporteSincronizacion();

  /**
   * Limpia un usuario huérfano de la base de datos.
   * Este método permite eliminar registros en BD que no tienen correspondencia en Keycloak.
   * 
   * @param keycloakId ID de Keycloak del usuario huérfano
   */
  void limpiarUsuarioHuerfanoBD(String keycloakId);
}

