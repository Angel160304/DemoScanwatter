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
        "/", "/index", "/login.html", "/registro", "/js/**", "/css/**", "/images/**"
    ).permitAll()
    .requestMatchers("/dashboard").authenticated()
    .anyRequest().permitAll()
)

// Ya no uses formLogin()

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