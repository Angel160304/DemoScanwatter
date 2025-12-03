package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/index.html"; // Página pública
    }
}
