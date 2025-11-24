package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitado temporalmente para simplicidad en pruebas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas que NO requieren autenticación
                .requestMatchers("/login", "/registro", "/css/**", "/js/**").permitAll() 
                // Todas las demás rutas, incluyendo /index y /dashboard, requieren autenticación
                .anyRequest().authenticated() 
            )
            // *** 🔑 AÑADIR LA CONFIGURACIÓN DEL FORMULARIO DE LOGIN AQUÍ ***
            .formLogin(login -> login
                .loginPage("/login") // 1. Indica la URL de la página de login
                .permitAll() // 2. Permite a TODOS acceder a la lógica del formulario de login
            );

        return http.build();
    }
}