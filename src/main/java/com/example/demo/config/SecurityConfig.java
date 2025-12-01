package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- 💡 EXCLUSIÓN DE RECURSOS ESTÁTICOS ---
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Ignora archivos estáticos
        return (web) -> web.ignoring().requestMatchers(
            new AntPathRequestMatcher("/js/**"), 
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/manifest.json")
        );
    }
    
    // --- FILTRO PRINCIPAL: PROTEGE EL DASHBOARD ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // Permitir API de login y páginas públicas
                .requestMatchers("/api/login/firebase", "/login.html", "/registro.html", "/index.html").permitAll() 
                
                // REQUERIR AUTENTICACIÓN para el Dashboard
                .requestMatchers("/dashboard").authenticated()
                
                // El resto de rutas requiere autenticación
                .anyRequest().authenticated()
            )
            // 💡 SOLUCIÓN CRÍTICA: Desactivar formLogin.
            .formLogin(form -> form.disable()) 
            
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}