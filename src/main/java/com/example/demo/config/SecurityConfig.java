package com.example.demo.config;

import com.example.demo.security.FirebaseAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, FirebaseAuthFilter firebaseAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/registro", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            // Agrega tu FirebaseAuthFilter antes del filtro de autenticación por defecto
            .addFilterBefore(firebaseAuthFilter, AbstractPreAuthenticatedProcessingFilter.class);

        return http.build();
    }
}
