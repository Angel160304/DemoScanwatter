// com.example.demo.config.SecurityConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig { // Nombre de clase corregido

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configuración de Autorización de Rutas
            .authorizeHttpRequests((requests) -> requests
                // Rutas Públicas: Permite acceso a login, registro, estáticos y el endpoint de validación de token.
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
                
                // 2. Proteger: TODAS las demás rutas, incluyendo "/", "/index", "/dashboard",
                //    requieren autenticación mediante la sesión de Spring Security.
                .anyRequest().authenticated()
            )
            // 3. Configuración de Login: Redirige aquí si se intenta acceder a una ruta protegida sin sesión.
            .formLogin((form) -> form
                .loginPage("/login") // La página de login que tienes en tu controlador
                .permitAll()
            )
            // 4. Configuración de Logout (para que Spring sepa cómo manejar el cierre de sesión)
            .logout((logout) -> logout.permitAll());
        
        // Es necesario deshabilitar CSRF si tu login es 100% API-based o si necesitas que funcione con POSTs simples.
        http.csrf(csrf -> csrf.disable()); 

        return http.build();
    }
}