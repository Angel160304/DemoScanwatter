package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig { // ABRIMOS LA CLASE

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/login.html", "/index.html", "/", "/css/**", "/js/**", "/img/**", "/manifest.json")
                    .permitAll()
                .requestMatchers("/dashboard")
                    .authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    } // CERRAMOS EL MÉTODO

} // <--- ¡ASEGÚRATE DE QUE ESTA LLAVE CIERRE LA CLASE!