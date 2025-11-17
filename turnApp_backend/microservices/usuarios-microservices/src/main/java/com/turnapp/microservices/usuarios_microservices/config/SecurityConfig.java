package com.turnapp.microservices.usuarios_microservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. Deshabilitar CSRF (Cross-Site Request Forgery) para APIs stateless
        .csrf(csrf -> csrf.disable())
        
        // 2. CORS está manejado por el API Gateway, no es necesario configurarlo aquí
        // .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // 3. Configurar el microservicio como un Servidor de Recursos OAuth2
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Convierte JWT a Authentication con roles
            )
        )

        // 4. Establecer la política de sesión como STATELESS
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 5. Proteger todos los endpoints
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated() // Requiere que cualquier petición esté autenticada
        );

    return http.build();
  }

  /**
   * Convierte el JWT de Keycloak en un objeto Authentication de Spring Security
   * extrayendo los roles tanto de realm_access como de resource_access
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
    return jwtConverter;
  }

  /**
   * Extrae los roles del token JWT de Keycloak.
   * Keycloak almacena los roles en:
   * - realm_access.roles (roles del realm)
   * - resource_access.{client-id}.roles (roles específicos del cliente)
   */
  @Bean
  public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    
    return jwt -> {
      // 1. Obtener authorities por defecto de Spring (scope claims)
      Collection<GrantedAuthority> defaultAuthorities = defaultGrantedAuthoritiesConverter.convert(jwt);
      
      // 2. Extraer roles del realm (realm_access.roles)
      Collection<GrantedAuthority> realmRoles = extractRealmRoles(jwt);
      
      // 3. Extraer roles de recursos (resource_access.{client}.roles)
      Collection<GrantedAuthority> resourceRoles = extractResourceRoles(jwt);
      
      // 4. Combinar todos los roles
      return Stream.of(defaultAuthorities, realmRoles, resourceRoles)
          .flatMap(Collection::stream)
          .collect(Collectors.toSet());
    };
  }

  /**
   * Extrae los roles del realm desde realm_access.roles
   */
  @SuppressWarnings("unchecked")
  private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    
    if (realmAccess == null || realmAccess.get("roles") == null) {
      return List.of();
    }
    
    Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
    return realmRoles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toList());
  }

  /**
   * Extrae los roles de los recursos desde resource_access.{client-id}.roles
   */
  @SuppressWarnings("unchecked")
  private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    
    if (resourceAccess == null) {
      return List.of();
    }
    
    return resourceAccess.values().stream()
        .filter(resource -> resource instanceof Map)
        .flatMap(resource -> {
          Map<String, Object> resourceMap = (Map<String, Object>) resource;
          Collection<String> roles = (Collection<String>) resourceMap.get("roles");
          return roles != null ? roles.stream() : Stream.empty();
        })
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toList());
  }

  /**
   * Configuración de CORS (deshabilitada porque el API Gateway maneja CORS globalmente)
   * Si necesitas habilitar CORS a nivel de microservicio, descomenta este método
   * y la línea .cors() en securityFilterChain
   */
  /*
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); // Frontend URLs
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(List.of("Authorization"));
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
  */
}
