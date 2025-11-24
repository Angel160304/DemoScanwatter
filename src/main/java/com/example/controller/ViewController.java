package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login"; // template login.html en templates/
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // template registro.html en templates/
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // template index.html en templates/
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // template dashboard.html en templates/
    }
}
