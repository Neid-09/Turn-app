package com.turnapp.microservice.turnos_microservice.shared.config;

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
 * Configuración de seguridad para el microservicio de turnos.
 * 
 * Valida tokens JWT provenientes de Keycloak y aplica autorización basada en roles:
 * 
 * ROL EMPLEADO:
 * - Puede consultar turnos (GET /api/turnos/**)
 * - Puede consultar sus propias asignaciones (GET /api/asignaciones/**)
 * - Puede consultar disponibilidades (GET /api/disponibilidades/**)
 * - Puede consultar descansos (GET /api/descansos/**)
 * - Puede consultar reemplazos (GET /api/reemplazos/**)
 * 
 * ROL ADMIN:
 * - Acceso completo a todos los endpoints (GET, POST, PUT, PATCH, DELETE)
 * - Gestión completa de turnos, asignaciones, disponibilidades, descansos y reemplazos
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
                // ========== ENDPOINTS DE TURNOS ==========
                // Solo consulta de turnos para EMPLEADO
                .requestMatchers(HttpMethod.GET, "/api/turnos/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                // Gestión de turnos solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/turnos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/turnos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/turnos/**")
                    .hasRole("ADMIN")
                
                // ========== ENDPOINTS DE ASIGNACIONES ==========
                // Consulta de asignaciones para EMPLEADO (pueden ver sus propias asignaciones)
                .requestMatchers(HttpMethod.GET, "/api/asignaciones/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                // Creación y modificación de asignaciones solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/asignaciones/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/asignaciones/**")
                    .hasRole("ADMIN")
                
                // ========== ENDPOINTS DE DISPONIBILIDADES ==========
                // Consulta de disponibilidades para EMPLEADO
                .requestMatchers(HttpMethod.GET, "/api/disponibilidades/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                // Gestión de disponibilidades solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/disponibilidades/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/disponibilidades/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/disponibilidades/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/disponibilidades/**")
                    .hasRole("ADMIN")
                
                // ========== ENDPOINTS DE DESCANSOS ==========
                // Consulta de descansos para EMPLEADO
                .requestMatchers(HttpMethod.GET, "/api/descansos/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                // Gestión de descansos solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/descansos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/descansos/**")
                    .hasRole("ADMIN")
                
                // ========== ENDPOINTS DE REEMPLAZOS ==========
                // Consulta de reemplazos para EMPLEADO
                .requestMatchers(HttpMethod.GET, "/api/reemplazos/**")
                    .hasAnyRole("EMPLEADO", "ADMIN")
                // Gestión de reemplazos solo para ADMIN
                .requestMatchers(HttpMethod.POST, "/api/reemplazos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/reemplazos/**")
                    .hasRole("ADMIN")
                
                // Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            );
        
        return http.build();
    }
}
