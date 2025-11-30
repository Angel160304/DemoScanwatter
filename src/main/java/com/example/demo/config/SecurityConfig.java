package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // 💡 #1: Permitir la API que crea la sesión (para que el fetch de auth.js funcione)
                .requestMatchers("/api/login/firebase").permitAll() 
                
                // 💡 #2: Permitir TODOS los recursos estáticos (CSS, JS, IMG)
                // Esto permite cargar /js/auth.js y SOLUCIONA el error 403 Forbidden.
                // Es seguro porque solo permite cargar archivos, no ejecuta lógica de servidor.
                .requestMatchers("/js/**", "/css/**", "/img/**", "/manifest.json", "/login.html", "/registro.html", "/index.html").permitAll()

                // 💡 #3: REQUERIR AUTENTICACIÓN para el Dashboard
                // Esta es la regla que verifica si hay una sesión válida de Spring Security.
                .requestMatchers("/dashboard").authenticated()
                
                // #4: Cualquier otra petición que no esté cubierta requiere autenticación por defecto.
                .anyRequest().authenticated()
            )
            // 💡 #5: Definir la página de login para la redirección automática de Spring Security
            .formLogin(form -> form
                .loginPage("/login.html") 
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable()); 
            
        return http.build();
    }
}