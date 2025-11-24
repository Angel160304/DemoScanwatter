package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html está en static
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // registro.html está en static
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // template en templates/index.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // template en templates/dashboard.html
    }
}
