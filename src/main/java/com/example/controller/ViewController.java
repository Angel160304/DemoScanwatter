package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // Opcional: manejar la raíz "/"
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index"; // Redirige a index.html
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
