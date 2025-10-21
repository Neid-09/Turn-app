package com.turnapp.microservices.usuarios_microservices.usuario.service;

import com.turnapp.microservices.usuarios_microservices.usuario.dto.RegistroUsuarioReq;

public interface IUsuarioService {

  void registrarUsuario(RegistroUsuarioReq request);

}
