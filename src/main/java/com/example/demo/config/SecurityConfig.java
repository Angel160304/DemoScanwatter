// com.example.demo.config.SecurityConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig { // Nombre de clase corregido

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers(
                "/",           // 👈 PERMITIR RUTA RAÍZ
                "/index",      // 👈 PERMITIR /index
                "/login", 
                "/registro", 
                "/js/**", 
                "/css/**", 
                "/images/**", 
                "/service-worker.js", 
                "/manifest.json",
                "/api/auth/verify-token"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin((form) -> form
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true) // Después del login va al dashboard
            .permitAll()
        )
        .logout((logout) -> logout.permitAll());

    http.csrf(csrf -> csrf.disable());

    return http.build();
}
}