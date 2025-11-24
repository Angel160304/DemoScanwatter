package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/manifest.json").permitAll()
                .anyRequest().authenticated() // todo lo demás requiere autenticación
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2.jwt()); // Firebase JWT será validado aquí

        return http.build();
    }
}
