package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 💡 HABILITA EL USO DE @PreAuthorize EN LOS CONTROLADORES
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configuración de Autorización de Rutas
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                    "/login",
                    "/registro",
                    "/js/**",
                    "/css/**",
                    "/images/**",
                    "/service-worker.js",
                    "/manifest.json",
                    "/api/auth/verify-token"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // 3. Configuración de Login
            .formLogin((form) -> form
                .loginPage("/login")
                .permitAll()
            )
            // 4. Configuración de Logout
            .logout((logout) -> logout.permitAll());

        // Deshabilitar CSRF (solo si sabes lo que haces)
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
