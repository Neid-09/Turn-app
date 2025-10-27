package com.turnapp.microservice.turnos_microservice.shared.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.extern.log4j.Log4j2;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de Feign Client para comunicación entre microservicios.
 * 
 * Configuraciones implementadas:
 * - Retry policy (reintentos automáticos)
 * - Timeouts (tiempos de espera)
 * - Logging (registro de llamadas)
 * 
 * @author TurnApp Team
 */
@Configuration
@EnableFeignClients(basePackages = "com.turnapp.microservice.turnos_microservice.integration.usuario.client")
@Log4j2
public class FeignConfig {
    
    /**
     * Configuración de reintentos para llamadas fallidas.
     * 
     * Parámetros:
     * - period: 100ms - Tiempo inicial entre reintentos
     * - maxPeriod: 1000ms - Tiempo máximo entre reintentos
     * - maxAttempts: 3 - Número máximo de intentos
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
    }
    
    /**
     * Configuración de timeouts para las peticiones.
     * 
     * - connectTimeout: 5 segundos para establecer conexión
     * - readTimeout: 10 segundos para leer respuesta
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }
    
    /**
     * Nivel de logging para Feign.
     * 
     * Niveles disponibles:
     * - NONE: Sin logs
     * - BASIC: Solo método, URL, código de respuesta y tiempo
     * - HEADERS: BASIC + headers de request y response
     * - FULL: Todo, incluyendo body (útil para debugging)
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // Cambiar a FULL para debugging detallado
    }
    
    /**
     * Interceptor para propagar el token JWT en llamadas entre microservicios.
     * 
     * Cuando un microservicio recibe una petición autenticada y necesita llamar
     * a otro microservicio, este interceptor automáticamente copia el token JWT
     * del SecurityContext y lo agrega al header Authorization de la petición Feign.
     * 
     * Esto es esencial para mantener el contexto de seguridad en comunicaciones
     * entre microservicios.
     * 
     * @return RequestInterceptor configurado para propagar JWT
     */
    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return requestTemplate -> {
            // Obtener el contexto de seguridad actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // Verificar si hay una autenticación válida con JWT
            if (authentication != null && authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
                
                // Extraer el token en formato String
                String tokenValue = jwtToken.getToken().getTokenValue();
                
                // Agregar el token al header Authorization
                requestTemplate.header("Authorization", "Bearer " + tokenValue);
                
                // Log para debugging (opcional)
                // System.out.println("🔐 Token JWT propagado a Feign Client");
            } else {
                // Log de advertencia si no hay token disponible
                log.warn("⚠️ No hay token JWT disponible para propagar en Feign Client");
            }
        };
    }
}
