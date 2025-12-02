package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

// 🛑 Importaciones para CORS y Session
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy; // NUEVO
import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define el SecurityFilterChain: Reglas de autorización y filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            // 1. Desactivar CSRF
            .csrf(AbstractHttpConfigurer::disable) 
            
            // 2. Aplicar la configuración de CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 🛑 CRÍTICO: Deshabilitar la gestión de sesiones automática (debe ser creada en el controlador)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            
            // 3. Definir reglas de autorización 
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso sin autenticar a la API de login y a recursos estáticos
                .requestMatchers("/api/login/**", "/login.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Requerir autenticación para cualquier otra solicitud
                .anyRequest().authenticated()
            )
            // Deshabilitar login basado en formulario y autenticación básica
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }


    /**
     * Define la configuración de CORS abierta para Render.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite el intercambio de cookies/sesiones (CRÍTICO)
        config.setAllowCredentials(true); 
        
        // Permitir todos los orígenes
        config.addAllowedOriginPattern("*"); 
        
        // Permitir todos los métodos y cabeceras
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}