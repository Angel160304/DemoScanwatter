package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 💡 ¡ESTA ES LA IMPORTACIÓN FALTANTE!
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- IGNORAR RECURSOS ESTÁTICOS ---
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            new AntPathRequestMatcher("/js/**"), 
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/manifest.json")
        );
    }
    
    // --- FILTRO PRINCIPAL (CONFIGURACIÓN DE PRUEBA ABIERTA) ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // 💡 Permitimos todas las peticiones GET (incluyendo /dashboard) para la prueba.
                .requestMatchers(HttpMethod.GET, "/**").permitAll() 
                
                // Permitimos la API de login de Firebase (POST)
                .requestMatchers(HttpMethod.POST, "/api/login/firebase").permitAll()
                
                // El resto (otras peticiones POST, PUT, DELETE, etc.) sigue protegido.
                .anyRequest().authenticated()
            )
            // Ya no necesitamos formLogin/logout ya que las rutas GET son abiertas
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}