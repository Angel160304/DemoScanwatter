package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /**
     * Redirige la raíz "/" a la página pública index.html.
     */
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index.html"; 
    }

    /**
     * Sirve la vista del Dashboard. Esta ruta requiere autenticación en SecurityConfig.
     * Retorna "dashboard" que se resuelve a templates/dashboard.html (ej. con Thymeleaf).
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; 
    }

    // 💡 Rutas añadidas por si Spring Security o el motor de plantillas tienen problemas:

    /**
     * Mapeo explícito para index.html (aunque esté en /static).
     */
    @GetMapping("/index.html")
    public String index() {
        return "index.html";
    }

    /**
     * Mapeo explícito para login.html (aunque esté en /static).
     */
    @GetMapping("/login.html")
    public String login() {
        return "login.html";
    }

    /**
     * Mapeo explícito para registro.html (aunque esté en /static).
     */
    @GetMapping("/registro.html")
    public String registro() {
        return "registro.html";
    }
}