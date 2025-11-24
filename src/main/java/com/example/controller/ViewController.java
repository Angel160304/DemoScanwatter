package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login"; // acceso público
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // acceso público
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // requiere autenticación
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // requiere autenticación
    }
}
