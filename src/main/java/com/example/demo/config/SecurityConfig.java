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
    return (web) -> web.ignoring().requestMatchers(
        new AntPathRequestMatcher("/auth.js"), // ⬅️ CRÍTICO
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
            .requestMatchers("/api/login/firebase", "/login.html", "/registro.html", "/index.html").permitAll() 
            .requestMatchers("/dashboard").authenticated()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form.disable()) 
        .logout(logout -> logout.permitAll())
        
        // 🛑 Desactivar CSRF COMPLETAMENTE Y GLOBALMENTE
        .csrf(csrf -> csrf.disable()); 
        
    return http.build();
}
}
