package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // 1. Permitimos TODAS las peticiones (ya lo tenías)
                .anyRequest().permitAll()
            )
            // 2. Deshabilitamos CSRF (ya lo tenías)
            .csrf(csrf -> csrf.disable())
            
            // 3. 💡 ¡NUEVO! Deshabilitar explícitamente el manejo de login por formularios.
            // Esto evita que Spring Security te redirija automáticamente si detecta
            // que falta un mecanismo de sesión.
            .formLogin(form -> form.disable());
            
        return http.build();
    }
}