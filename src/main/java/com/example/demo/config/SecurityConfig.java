// Archivo: com.example.demo.config.SecurityConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 🛑 Importaciones para CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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
            // 1. 🛑 CRÍTICO: Desactivar CSRF (necesario para API REST sin tokens CSRF)
            .csrf(csrf -> csrf.disable()) 
            
            // 2. 🛑 CRÍTICO: Aplicar la configuración de CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 3. Definir reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso sin autenticar a la API de login y a recursos estáticos
                .requestMatchers("/api/login/**", "/login.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Requerir autenticación para cualquier otra solicitud
                .anyRequest().authenticated()
            );
            // Si no defines un .formLogin() o .httpBasic(), Spring Security usará solo la gestión de sesiones.

        return http.build();
    }


    /**
     * Define la configuración de CORS (necesario si el frontend y backend no están en el mismo subdominio).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 🛑 CONFIGURACIÓN DE CORS ABIERTA PARA DESARROLLO/RENDER
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite el intercambio de cookies/sesiones (CRÍTICO para Spring Security Session)
        config.setAllowCredentials(true); 
        
        // ⚠️ Usar patrones de origen si solo quieres tu dominio de Render:
        // config.setAllowedOriginPatterns(Arrays.asList("https://demoscanwatter.onrender.com", "http://localhost:8080"));
        // Pero para máxima compatibilidad, usamos * con patrón:
        config.addAllowedOriginPattern("*"); 
        
        // Permitir todos los métodos (POST, GET, etc.)
        config.addAllowedMethod("*");
        // Permitir todas las cabeceras
        config.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", config); // Aplicar a todas las rutas
        return source;
    }
    
    // NOTA: No se necesita un PasswordEncoder en este proyecto ya que Firebase maneja las credenciales.
}