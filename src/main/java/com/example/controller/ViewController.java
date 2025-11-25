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
        return "login.html"; // Devuelve login desde static
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; 
    }

    @GetMapping("/index")
    public String index() {
        return "index"; 
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Protegida por Spring Security
    }
}
