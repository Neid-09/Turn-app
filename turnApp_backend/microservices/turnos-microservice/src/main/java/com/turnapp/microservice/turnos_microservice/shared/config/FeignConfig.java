package com.turnapp.microservice.turnos_microservice.shared.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
