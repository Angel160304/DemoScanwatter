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
    
    // Inyección del filtro de Firebase
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
        
        // 🛑 CRÍTICO: Definir permisos para estáticos primero
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // Acceso público y estáticos con comodines más amplios
                    "/", 
                    "/api/login/**", 
                    "/api/registro/**",
                    "/*.html",       // Todos los HTML en la raíz (login.html, registro.html, etc.)
                    "/*.ico", "/favicon.ico", // Favicon (doble seguridad)
                    "/*.json", "/*.css", "/*.js", // Archivos estáticos en la raíz
                    "/images/**", "/css/**", "/js/**" // Carpetas estáticas
                ).permitAll()
                
                // Requerir autenticación para el resto de las rutas (ej: /dashboard, /api/data)
                .anyRequest().authenticated()
            );

        // Luego, aplicar las configuraciones de seguridad
        http
            // 1. Desactivar CSRF
            .csrf(AbstractHttpConfigurer::disable) 
            
            // 2. Aplicar la configuración de CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 3. Stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            
            // 4. Añadir el filtro de Firebase
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class) 
            
            // 5. Deshabilitar login basado en formulario y autenticación básica
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