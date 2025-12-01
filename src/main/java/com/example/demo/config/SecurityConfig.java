package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 💡 ¡ESTA ES LA IMPORTACIÓN FALTANTE!
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// Archivo: com.example.demo.config.SecurityConfig.java (VERSIÓN SEGURA)

// ... (imports) ...

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 💡 Dejamos la exclusión de estáticos (WebSecurityCustomizer) para evitar el 302
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
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
                // Permitir API de login y las páginas HTML
                .requestMatchers("/api/login/firebase", "/login.html", "/registro.html", "/index.html").permitAll() 
                
                // REQUERIR AUTENTICACIÓN para el Dashboard
                .requestMatchers("/dashboard").authenticated()
                
                // El resto de rutas requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.loginPage("/login.html").permitAll())
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}