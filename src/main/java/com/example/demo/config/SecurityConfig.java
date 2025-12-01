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
        // Ignora archivos estáticos para evitar que Spring Security los intercepte con 302/403
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
                // Permitir API de login y páginas públicas (login/registro/index)
                .requestMatchers("/api/login/firebase", "/login.html", "/registro.html", "/index.html").permitAll() 
                
                // REQUERIR AUTENTICACIÓN para el Dashboard
                .requestMatchers("/dashboard").authenticated()
                
                // El resto de rutas requiere autenticación
                .anyRequest().authenticated()
            )
            // 💡 SOLUCIÓN CRÍTICA: Desactivar formLogin.
            // Esto evita que Spring Security interfiera con la sesión que creas en tu controlador REST.
            .formLogin(form -> form.disable()) 
            
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}