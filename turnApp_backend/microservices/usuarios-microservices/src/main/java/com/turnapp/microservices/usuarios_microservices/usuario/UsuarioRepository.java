package com.turnapp.microservices.usuarios_microservices.usuario;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turnapp.microservices.usuarios_microservices.usuario.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

}
