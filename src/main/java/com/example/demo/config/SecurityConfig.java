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
                // Rutas públicas
                .requestMatchers(
                    "/login.html",
                    "/registro.html",
                    "/index.html",
                    "/js/**",
                    "/css/**",
                    "/images/**",
                    "/service-worker.js",
                    "/manifest.json",
                    "/api/auth/verify-token"
                ).permitAll()
                .anyRequest().authenticated() // Todo lo demás protegido
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        http.csrf(csrf -> csrf.disable()); // Para login API

        return http.build();
    }
}
