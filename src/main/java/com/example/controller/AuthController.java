package com.example.demo.controller;

import com.example.demo.service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private FirebaseAuthService authService;

    // ===== REGISTRO =====
    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password) {
        try {
            String uid = authService.registrarUsuario(email, password);
            return "Usuario registrado con UID: " + uid;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ===== LOGIN =====
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        try {
            boolean valido = authService.loginUsuario(email, password);

            if (!valido) {
                return "Error: Credenciales incorrectas";
            }

            return "Login exitoso";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
