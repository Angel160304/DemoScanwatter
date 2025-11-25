package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index.html"; // página pública
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // index.html en static
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // dashboard protegido
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // registro.html en static
    }

    // ❌ NO mapear /login, solo usar /login.html
}
