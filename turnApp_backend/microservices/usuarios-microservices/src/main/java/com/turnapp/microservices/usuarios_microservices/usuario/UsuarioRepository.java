package com.turnapp.microservices.usuarios_microservices.usuario;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.turnapp.microservices.usuarios_microservices.usuario.model.Usuario;

/**
 * Repositorio para la entidad Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

  /**
   * Busca un usuario por su ID de Keycloak.
   * 
   * @param keycloakId ID del usuario en Keycloak
   * @return Optional con el usuario si existe
   */
  Optional<Usuario> findByKeycloakId(String keycloakId);

  /**
   * Busca un usuario por su código de empleado.
   * 
   * @param codigoEmpleado Código de empleado
   * @return Optional con el usuario si existe
   */
  Optional<Usuario> findByCodigoEmpleado(String codigoEmpleado);

  /**
   * Busca un usuario por su número de identificación.
   * 
   * @param numeroIdentificacion Número de identificación
   * @return Optional con el usuario si existe
   */
  Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);

  /**
   * Verifica si existe un usuario con el código de empleado dado.
   * 
   * @param codigoEmpleado Código de empleado
   * @return true si existe, false en caso contrario
   */
  boolean existsByCodigoEmpleado(String codigoEmpleado);

  /**
   * Verifica si existe un usuario con el número de identificación dado.
   * 
   * @param numeroIdentificacion Número de identificación
   * @return true si existe, false en caso contrario
   */
  boolean existsByNumeroIdentificacion(String numeroIdentificacion);

  /**
   * Verifica si existe un usuario con el username dado.
   * 
   * @param username Nombre de usuario
   * @return true si existe, false en caso contrario
   */
  boolean existsByUsername(String username);

  /**
   * Verifica si existe un usuario con el email dado.
   * 
   * @param email Email del usuario
   * @return true si existe, false en caso contrario
   */
  boolean existsByEmail(String email);

  /**
   * Busca usuarios por término de búsqueda en código de empleado, cargo o número de identificación.
   * 
   * @param searchTerm Término de búsqueda
   * @param pageable Información de paginación
   * @return Página de usuarios que coinciden
   */
  @Query("SELECT u FROM Usuario u WHERE " +
      "LOWER(u.codigoEmpleado) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.cargo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.numeroIdentificacion) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  Page<Usuario> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}

