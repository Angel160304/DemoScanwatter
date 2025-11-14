package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Redirige la raíz "/" a /login
    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }
}
