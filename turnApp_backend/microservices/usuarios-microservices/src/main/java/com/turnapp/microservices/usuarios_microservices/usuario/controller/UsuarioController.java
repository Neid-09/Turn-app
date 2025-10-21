package com.turnapp.microservices.usuarios_microservices.usuario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;
import com.turnapp.microservices.usuarios_microservices.usuario.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
  private final IUsuarioService usuarioService;

  @GetMapping
  public String hola() {
    return "Hola desde el microservicio de usuarios";
  }

  @PostMapping("/registro")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public ResponseEntity<String> registrarNuevoUsuario(@RequestBody RegistroUsuarioReq request) {
    usuarioService.registrarUsuario(request);
    return ResponseEntity.status(201).body("Usuario creado exitosamente.");
  }
}
