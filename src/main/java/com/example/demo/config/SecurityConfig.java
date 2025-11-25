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
            // 1️⃣ Autorización de rutas
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(
                    "/login",         // Login mapeado en controlador
                    "/login.html",    // Login en static
                    "/registro",
                    "/js/**",
                    "/css/**",
                    "/images/**",
                    "/service-worker.js",
                    "/manifest.json",
                    "/api/auth/verify-token"
                ).permitAll()
                .anyRequest().authenticated() // Todo lo demás protegido
            )
            // 2️⃣ Configuración de Login
            .formLogin(form -> form
                .loginPage("/login.html") // Página de login real
                .permitAll()
            )
            // 3️⃣ Configuración de Logout
            .logout(logout -> logout.permitAll());

        // 4️⃣ CSRF deshabilitado si tu login es API-based
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
