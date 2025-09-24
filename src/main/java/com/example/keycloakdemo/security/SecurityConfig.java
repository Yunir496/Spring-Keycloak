package com.example.keycloakdemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .cors(Customizer.withDefaults())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(reg -> reg
              .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
              .requestMatchers("/actuator/health").permitAll()
              .requestMatchers("/api/admin/**").hasRole("ADMIN")
              .requestMatchers("/api/user/**").hasRole("USER")
              .requestMatchers("/api/moderator/**").hasRole("MODERATOR")
              .anyRequest().authenticated())
          .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
          );
        return http.build();
    }
}
