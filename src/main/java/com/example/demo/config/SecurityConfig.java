package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Archivo: com.example.demo.config.SecurityConfig.java (VERIFICACIÓN)

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(requests -> requests
            // ESTO DEBE SER PERMITIDO: La API que crea la sesión de Spring
            .requestMatchers("/api/login/firebase").permitAll() 
            // Esto es el resto de páginas públicas
            .requestMatchers("/login.html", "/index.html", "/", "/css/**", "/js/**", "/img/**").permitAll()
            // ESTO DEBE ESTAR PROTEGIDO
            .requestMatchers("/dashboard").authenticated()
            .anyRequest().authenticated()
        )
        // ... (El resto de la configuración de formLogin y csrf)
        // ...
        .csrf(csrf -> csrf.disable());
        
    return http.build();
}
}