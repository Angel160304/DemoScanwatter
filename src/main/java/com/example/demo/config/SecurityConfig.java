package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
// Archivo: com.example.demo.config.SecurityConfig.java

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // 💡 Nuevo import
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // 💡 Nuevo import

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ... (Tu método securityFilterChain se mantiene igual, protegiendo el Dashboard) ...

    // --- 💡 SOLUCIÓN FINAL AL 302/MIME TYPE ERROR ---
    // En SecurityConfig.java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(
        // Rutas para la subcarpeta /js/
        new AntPathRequestMatcher("/js/**"), 
        // Rutas para archivos .js en la raíz del servidor (por si es lo que Render o Spring ven)
        new AntPathRequestMatcher("/*.js"), 
        // Otros estáticos
        new AntPathRequestMatcher("/css/**"),
        new AntPathRequestMatcher("/img/**"),
        new AntPathRequestMatcher("/manifest.json")
    );
}

    // --- Tu método securityFilterChain DEBE seguir protegiendo el Dashboard ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // Permitimos la API de login y las páginas HTML estáticas
                .requestMatchers("/api/login/firebase", "/login.html", "/registro.html", "/index.html").permitAll() 
                // La regla para estáticos aquí es opcional ahora, pero la mantenemos limpia:
                // .requestMatchers("/js/**", "/css/**", "/img/**", "/manifest.json").permitAll() // OPTIONAL
                
                // REQUERIR AUTENTICACIÓN para el Dashboard
                .requestMatchers("/dashboard").authenticated()
                
                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.loginPage("/login.html").permitAll())
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}