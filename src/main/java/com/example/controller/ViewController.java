package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index.html"; // Página pública
    }

    // ❌ NO MAPEES login ni registro
    // Están en /static y se sirven automáticamente

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // dashboard.html en templates
    }
}
