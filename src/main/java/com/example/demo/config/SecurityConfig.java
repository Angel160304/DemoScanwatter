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
            // 1️⃣ Rutas públicas
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(
                    "/login.html",    // Login público en static
                    "/registro.html", // Registro público si existe
                    "/js/**",
                    "/css/**",
                    "/images/**",
                    "/service-worker.js",
                    "/manifest.json",
                    "/api/auth/verify-token"
                ).permitAll()
                .anyRequest().authenticated() // Todo lo demás protegido
            )
            // 2️⃣ Configuración de login
            .formLogin(form -> form
                .loginPage("/login.html") // Apunta al login en static
                .permitAll()
            )
            // 3️⃣ Configuración de logout
            .logout(logout -> logout.permitAll());

        // 4️⃣ Deshabilitar CSRF si tu login es API-based
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
