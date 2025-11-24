package com.turnapp.microservice.horarios_microservice.integration.usuario.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado para obtener el nombre completo del empleado.
 * Solo contiene los campos necesarios para mostrar el nombre en el frontend.
 * 
 * @author TurnApp Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioBasicoResponse {
    
    private String firstName;
    private String lastName;
}
