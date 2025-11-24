package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html en templates/
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // registro.html en templates/
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // index.html en templates/
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // dashboard.html en templates/
    }
}
