package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
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
        return (web) -> web.ignoring().requestMatchers(
            new AntPathRequestMatcher("/js/**"), 
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/manifest.json")
        );
    }
    
    // --- FILTRO PRINCIPAL: PROTEGE EL DASHBOARD Y GESTIONA EL LOGIN ---
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
            // 💡 MODIFICACIÓN CLAVE: Definir la URL de éxito explícita.
            .formLogin(form -> form
                .loginPage("/login.html")
                .defaultSuccessUrl("/dashboard", true) // ⬅️ Redirige siempre a /dashboard después de un login exitoso
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}