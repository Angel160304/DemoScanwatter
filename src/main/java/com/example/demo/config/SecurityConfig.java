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
            // 💡 #1: Permitir el acceso a login y todos los estáticos
            // Usaremos el orden de las reglas para dar prioridad a lo que queremos permitir
            .requestMatchers("/api/login/firebase", 
                             "/login.html", 
                             "/registro.html", 
                             "/index.html",
                             "/css/**", 
                             "/img/**", 
                             "/manifest.json",
                             "/*.js",       // Para archivos JS en la raíz (ej: /auth.js si estuviera ahí)
                             "/js/**")      // 💡 ESTO ES CRÍTICO: Cubre la carpeta /js/
                .permitAll()

            // 💡 #2: REQUERIR AUTENTICACIÓN para el Dashboard
            .requestMatchers("/dashboard").authenticated()
            
            // #3: El resto de rutas deben estar protegidas
            .anyRequest().authenticated()
        )
        // ... (resto de formLogin y csrf)
        .formLogin(form -> form
            .loginPage("/login.html") 
            .permitAll()
        )
        .logout(logout -> logout.permitAll())
        .csrf(csrf -> csrf.disable()); 
        
    return http.build();
}
}