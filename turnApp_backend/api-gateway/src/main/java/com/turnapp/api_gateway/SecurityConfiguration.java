package com.turnapp.api_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
      http.authorizeExchange(exchange -> exchange
              .pathMatchers("/public/**", "/login").permitAll()
              .anyExchange().authenticated())
              .oauth2Login(Customizer.withDefaults())
              .csrf(csrf -> csrf.disable());
    return http.build();
  }

}
