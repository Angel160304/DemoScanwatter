package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final FirebaseTokenFilter firebaseTokenFilter; 

    @Autowired
    public SecurityConfig(FirebaseTokenFilter firebaseTokenFilter) {
        this.firebaseTokenFilter = firebaseTokenFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            .authorizeHttpRequests(auth -> auth
                
                // 1. PERMITIR ACCESO A RECURSOS ESTÁTICOS Y PÚBLICOS
                .requestMatchers(
                    // Directorios estáticos comunes
                    "/css/**", 
                    "/js/**", 
                    "/images/**",
                    "/*.ico", 
                    
                    // Páginas HTML de inicio y autenticación
                    "/", // Raíz del sitio
                    "/*.html", // Todos los archivos HTML en la raíz
                    
                    // Rutas de API públicas (Login/Registro)
                    "/api/login/**", 
                    "/api/registro/**",
                    "/api/test/publica"
                ).permitAll()
                
                // 2. RUTAS QUE REQUIEREN AUTENTICACIÓN
                .requestMatchers("/dashboard", "/index", "/api/check").authenticated()
                
                // 3. CUALQUIER OTRA RUTA requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Inicio de las configuraciones de seguridad
            .csrf(AbstractHttpConfigurer::disable) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            // Añadir el filtro de Firebase antes del filtro básico de autenticación
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class) 
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
        
        return http.build();
    }

    // Configuración de CORS
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
