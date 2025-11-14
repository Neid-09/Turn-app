package com.turnapp.microservice.horarios_microservice.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el microservicio de horarios.
 * 
 * Valida tokens JWT provenientes de Keycloak y aplica autorización basada en roles:
 * 
 * ROL EMPLEADO:
 * - Puede consultar horarios (GET /api/horarios/**)
 * - Puede ver su calendario personal
 * 
 * ROL ADMIN:
 * - Acceso completo a todos los endpoints (GET, POST, PUT, PATCH, DELETE)
 * - Gestión completa de horarios (creación, publicación, modificación)
 * - Generación de reportes consolidados
 * 
 * @author TurnApp Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * - Desactiva CSRF (no necesario para APIs REST stateless)
     * - Configura política de sesión como STATELESS
     * - Define reglas de autorización por endpoint y rol
     * - Configura validación de tokens JWT con Keycloak
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // ========== ENDPOINTS DE HORARIOS ==========
                // Solo consulta de horarios para EMPLEADO
                .requestMatchers(HttpMethod.GET, "/api/horarios/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                
                // Gestión de horarios solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/horarios/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/horarios/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/horarios/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/horarios/**")
                    .hasRole("ADMIN")
                
                // ========== ENDPOINTS DE REPORTES ==========
                .requestMatchers(HttpMethod.GET, "/api/reportes/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                
                // Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            );
        
        return http.build();
    }
}
