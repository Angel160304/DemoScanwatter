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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
import org.springframework.beans.factory.annotation.Autowired; 

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // 1. Inyección del nuevo filtro de Firebase
    private final FirebaseTokenFilter firebaseTokenFilter; 

    @Autowired
    public SecurityConfig(FirebaseTokenFilter firebaseTokenFilter) {
        this.firebaseTokenFilter = firebaseTokenFilter;
    }
    
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
            
            // 3. Añadir el filtro de Firebase ANTES de la verificación estándar de Spring
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class) 
            
            // 4. Definir reglas de autorización 
            .authorizeHttpRequests(auth -> auth
                // 🛑 RUTAS PÚBLICAS Y ESTATICAS: EXCLUSIÓN ROBUSTA
                .requestMatchers(
                    // Rutas de API y raíz
                    "/", 
                    "/api/login/**", 
                    "/api/registro/**", 
                    
                    // Comodín para cualquier archivo HTML en la raíz
                    "/*.html",       
                    
                    // Acceso a archivos estáticos en la raíz (incluye favicon y manifest)
                    "/*.ico", 
                    "/favicon.ico", // Ruta exacta para el favicon (doble seguridad)
                    "/*.json", 
                    "/*.css", 
                    "/*.js",
                    
                    // Comodines de subdirectorio (el doble * es clave)
                    "/images/**", 
                    "/css/**", 
                    "/js/**" 
                ).permitAll()
                
                // Requerir autenticación para cualquier otra solicitud (ej: /dashboard)
                .anyRequest().authenticated()
            )
            // Deshabilitar login basado en formulario y autenticación básica
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
        
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