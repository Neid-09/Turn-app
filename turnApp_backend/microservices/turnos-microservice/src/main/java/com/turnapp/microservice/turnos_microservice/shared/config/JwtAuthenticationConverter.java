package com.turnapp.microservice.turnos_microservice.shared.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Conversor personalizado para extraer roles de Keycloak desde el token JWT.
 * 
 * Keycloak almacena los roles del cliente en la estructura:
 * {
 *   "resource_access": {
 *     "turn-app-client": {
 *       "roles": ["ADMIN", "EMPLEADO"]
 *     }
 *   },
 *   "realm_access": {
 *     "roles": ["offline_access", "uma_authorization"]
 *   }
 * }
 * 
 * Este conversor extrae los roles del cliente y del realm, agregando el prefijo "ROLE_"
 * requerido por Spring Security.
 * 
 * @author TurnApp Team
 */
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Nombre del cliente en Keycloak.
     * Este debe coincidir con el client-id configurado en tu aplicación.
     */
    private static final String CLIENT_ID = "turn-app-client";

    /**
     * Convierte un JWT en un token de autenticación con roles extraídos.
     * 
     * @param jwt Token JWT de Keycloak
     * @return JwtAuthenticationToken con las autoridades (roles) extraídas
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    /**
     * Extrae las autoridades (roles) del JWT.
     * Busca roles tanto en resource_access como en realm_access.
     * 
     * @param jwt Token JWT
     * @return Colección de autoridades con el prefijo ROLE_
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        
        // Extraer roles del cliente específico (resource_access)
        roles.addAll(extractClientRoles(jwt));
        
        // Extraer roles del realm (realm_access)
        roles.addAll(extractRealmRoles(jwt));
        
        // Convertir roles a autoridades de Spring Security con prefijo ROLE_
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    /**
     * Extrae roles del cliente desde resource_access.
     * 
     * Estructura esperada:
     * resource_access -> {client_id} -> roles -> ["ADMIN", "EMPLEADO"]
     * 
     * @param jwt Token JWT
     * @return Set de roles del cliente
     */
    @SuppressWarnings("unchecked")
    private Set<String> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        
        if (resourceAccess == null) {
            return Collections.emptySet();
        }
        
        Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
        
        if (clientResource == null) {
            return Collections.emptySet();
        }
        
        List<String> roles = (List<String>) clientResource.get("roles");
        
        return roles != null ? new HashSet<>(roles) : Collections.emptySet();
    }

    /**
     * Extrae roles del realm desde realm_access.
     * 
     * Estructura esperada:
     * realm_access -> roles -> ["offline_access", "uma_authorization", ...]
     * 
     * @param jwt Token JWT
     * @return Set de roles del realm
     */
    @SuppressWarnings("unchecked")
    private Set<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        
        if (realmAccess == null) {
            return Collections.emptySet();
        }
        
        List<String> roles = (List<String>) realmAccess.get("roles");
        
        return roles != null ? new HashSet<>(roles) : Collections.emptySet();
    }
}
