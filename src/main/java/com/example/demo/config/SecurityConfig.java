// Archivo: com.example.demo.config.SecurityConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;


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
            
            // 3. 🛑 CRÍTICO DE PRUEBA: Permitir TODAS las solicitudes
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // ⬅️ ¡TODO PERMITIDO PARA DIAGNOSTICAR EL 403!
            )
            // 4. Asegurar que Spring use el contexto de sesión estándar
            .securityContext((securityContext) -> securityContext
                .securityContextRepository(new HttpSessionSecurityContextRepository())
            )
            // Deshabilitar login/basic authentication
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);


        return http.build();
    }


    /**
     * Define la configuración de CORS abierta.
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