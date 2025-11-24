package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String root() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // Protegido por Security + Filter
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Protegido por Security + Filter
    }
}
