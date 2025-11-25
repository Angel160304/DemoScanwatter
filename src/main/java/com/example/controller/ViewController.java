package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index"; // Redirige a index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login.html"; // Para Spring Security y QR
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // Muestra registro.html
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // Muestra index.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Muestra dashboard.html
    }
}
