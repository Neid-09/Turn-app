package com.turnapp.microservices.usuarios_microservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. Deshabilitar CSRF (Cross-Site Request Forgery) para APIs stateless
        .csrf(csrf -> csrf.disable())

        // 2. Configurar el microservicio como un Servidor de Recursos OAuth2
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> {
            }) // Habilita la validación de tokens JWT con la configuración del .yml
        )

        // 3. Establecer la política de sesión como STATELESS
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 4. Proteger todos los endpoints
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated() // Requiere que cualquier petición esté autenticada
        );

    return http.build();
  }
}
