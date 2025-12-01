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

    // --- 💡 SOLUCIÓN: IGNORAR RECURSOS ESTÁTICOS PARA ELIMINAR EL ERROR 302 ---
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            // 💡 Nuevo nombre para el archivo JS (si lo renombras a 'app.js')
            new AntPathRequestMatcher("/js/app.js"), 
            
            // Regla general para toda la carpeta JS (debe ser suficiente)
            new AntPathRequestMatcher("/js/**"), 
            
            // Reglas para otros estáticos
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/manifest.json"),
            
            // Regla para cualquier JS en la raíz (por seguridad)
            new AntPathRequestMatcher("/*.js") 
        );
    }

    // --- FILTRO PRINCIPAL: PROTEGE EL DASHBOARD Y PERMITE EL LOGIN DE LA API ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // Permitimos la API de login de Firebase y las páginas HTML estáticas
                .requestMatchers("/api/login/firebase", "/login.html", "/registro.html", "/index.html").permitAll() 
                
                // REQUERIR AUTENTICACIÓN para el Dashboard
                .requestMatchers("/dashboard").authenticated()
                
                // El resto de rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.loginPage("/login.html").permitAll())
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}