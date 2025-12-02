// Archivo: com.example.demo.config.SecurityConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// ... (Otras importaciones)

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
            
            // 🛑 CRÍTICO: Asegurar que el servidor es completamente Stateless (sin sesiones HTTP)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            
            // 3. Definir reglas de autorización 
            .authorizeHttpRequests(auth -> auth
                // 🛑 RUTAS PÚBLICAS Y ESTATICAS: EXCLUSIÓN ROBUSTA
                .requestMatchers(
                    "/", // La ruta raíz
                    "/api/login/**", 
                    "/api/registro/**", 
                    "/login.html", 
                    "/registro.html",   // ⬅️ Permiso explícito para la página
                    "/index.html",      // Página principal
                    "/favicon.ico", 
                    "/manifest.json",
                    "/images/**",       // Cualquier imagen
                    "/css/**",          // Todos los archivos CSS
                    "/js/**",           // Todos los archivos JS
                    "/*.css",           // CSS en la raíz
                    "/*.js",            // JS en la raíz (como auth.js)
                    "/*.html"           // HTML en la raíz
                ).permitAll()
                
                // Requerir autenticación (via el filtro JWT) para cualquier otra solicitud
                .anyRequest().authenticated()
            )
            // Deshabilitar login basado en formulario y autenticación básica
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
        
        // ... (Si usas un filtro JWT, debes añadirlo aquí)
        
        return http.build();
    }


    /**
     * Define la configuración de CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); 
        config.addAllowedOriginPattern("*"); 
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}