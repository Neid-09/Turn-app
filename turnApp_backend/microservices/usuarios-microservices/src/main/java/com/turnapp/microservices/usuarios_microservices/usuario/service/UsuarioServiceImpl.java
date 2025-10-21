package com.turnapp.microservices.usuarios_microservices.usuario.service;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.turnapp.microservices.usuarios_microservices.usuario.UsuarioRepository;
import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.model.Usuario;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

  private final Keycloak keycloak;
  private final UsuarioRepository usuarioRepository;

  @Value("${keycloak.admin-client.realm}")
  private String realm;

  @Override
  public void registrarUsuario(RegistroUsuarioReq request) {
    // --- 1. Crear la identidad en Keycloak ---
    UserRepresentation userRepresentation = buildUserRepresentation(request);
    UsersResource usersResource = keycloak.realm(realm).users();
    Response response = usersResource.create(userRepresentation);

    if (response.getStatus() != 201) {
      // Manejar error (ej. usuario ya existe en Keycloak)
      throw new RuntimeException("Error al crear usuario en Keycloak. Status: " + response.getStatus());
    }

    // --- 2. Obtener el ID de Keycloak del nuevo usuario ---
    String keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

    // --- 3. Crear y guardar los datos laborales en nuestra BD ---
    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setKeycloakId(keycloakId);
    nuevoUsuario.setCodigoEmpleado(request.codigoEmpleado());
    nuevoUsuario.setCargo(request.cargo());
    nuevoUsuario.setFechaContratacion(request.fechaContratacion());
    nuevoUsuario.setRolApp(request.rolApp());
    nuevoUsuario.setNumeroIdentificacion(request.numeroIdentificacion());
    nuevoUsuario.setTelefono(request.telefono());

    usuarioRepository.save(nuevoUsuario);
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
    user.setEmailVerified(true); // Opcional

    return user;
  }

}
