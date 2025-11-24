package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // IMPORTANTE
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // ¡ESTA ANOTACIÓN ES NECESARIA PARA ACTIVAR LA SEGURIDAD!
public class SecurityConfig {

    // Necesitamos inyectar la URL de éxito para redirigir después de iniciar la sesión de Spring
    // Esta propiedad debe estar en application.properties (ej: app.default.success.url=/index)
    @Value("${app.default.success.url}")
    private String defaultSuccessUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (Crucial si estás usando un frontend externo/AJAX)
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(auth -> auth
                // 2. RUTAS PÚBLICAS: Acceso sin autenticación
                // Incluimos tu API de autenticación para que el frontend pueda enviar el token a /verify-token
                .requestMatchers(
                    "/login", 
                    "/registro", 
                    "/api/auth/**", // <-- ¡CRÍTICO! Permite acceder a /api/auth/verify-token
                    "/css/**", 
                    "/js/**", 
                    "/manifest.json"
                ).permitAll() 
                
                // 3. RUTAS PROTEGIDAS: Cualquier otra URL requiere autenticación
                .anyRequest().authenticated() 
            )
            
            .formLogin(login -> login
                // Redirige a /login si el usuario no está autenticado
                .loginPage("/login") 
                // Define a dónde redirigir después de que Spring Security establece la sesión
                .defaultSuccessUrl(defaultSuccessUrl, true) 
                .permitAll() 
            )
            
            // 4. Configuración de Logout
            .logout(logout -> logout
                .logoutUrl("/logout") 
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}