// Archivo: com.example.demo.config.WebConfig.java (CREAR ESTE ARCHIVO)

package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        
        // Mapea la raíz ("/") directamente al archivo estático login.html
        // Usamos "forward" para evitar la redirección HTTP 302
        registry.addViewController("/").setViewName("forward:/login.html"); 
        
        // Mapeo del Dashboard (Busca templates/dashboard.html)
        registry.addViewController("/dashboard").setViewName("dashboard");
        
        // Mapeo de otras páginas estáticas (opcional)
        registry.addViewController("/registro.html").setViewName("forward:/registro.html");
        registry.addViewController("/index.html").setViewName("forward:/index.html");
    }
}