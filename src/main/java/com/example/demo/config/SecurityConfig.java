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
                // Permitir acceso a la API del token para crear la sesión
                .requestMatchers("/api/login/firebase").permitAll() 
                // Permitir acceso a login, index y estáticos
                .requestMatchers("/login.html", "/index.html", "/", "/css/**", "/js/**", "/img/**").permitAll()
                // REQUERIR AUTENTICACIÓN para el Dashboard
                .requestMatchers("/dashboard").authenticated()
                // El resto debe requerir autenticación (o puedes poner .anyRequest().permitAll() si quieres)
                .anyRequest().authenticated()
            )
            // Definir la página de login para la redirección (si el usuario no está logueado)
            .formLogin(form -> form.loginPage("/login.html").permitAll())
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
}