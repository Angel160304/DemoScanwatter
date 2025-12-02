package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

// 🛑 Importaciones para CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Nuevo

// Importación para el filtro JWT (necesitas crear esta clase)
// import com.example.demo.security.FirebaseTokenFilter; 


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // Si implementas el filtro JWT, debes inyectarlo aquí:
    // @Autowired
    // private FirebaseTokenFilter firebaseTokenFilter; 

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
                // Permitir acceso sin autenticar solo al endpoint de validación de token y estáticos
                .requestMatchers("/api/login/**", "/login.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Requerir autenticación (via el filtro JWT) para cualquier otra solicitud
                .anyRequest().authenticated()
            )
            // Deshabilitar login basado en formulario y autenticación básica
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        // 🛑 Paso pendiente: Añadir el filtro de autenticación JWT
        // http.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * Define la configuración de CORS abierta para Render.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite el intercambio de encabezados (necesario para el token JWT)
        config.setAllowCredentials(true); 
        
        // Permitir todos los orígenes
        config.addAllowedOriginPattern("*"); 
        
        // Permitir todos los métodos y cabeceras (incluyendo 'Authorization')
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}