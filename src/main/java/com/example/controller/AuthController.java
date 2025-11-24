package com.example.demo.controller;

import com.example.demo.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private FirebaseAuthService authService;

    // Puedes dejar el registro si tu frontend lo usa como API
    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password) {
        // Asumiendo que esta lógica llama a la API REST de Firebase o usa Admin SDK
        return "Método de registro pendiente de implementar con Admin SDK o solo en cliente.";
    }

    // ===== ENDPOINT PARA VALIDAR EL TOKEN Y CREAR LA SESIÓN DE SPRING SECURITY =====
    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyTokenAndLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        
        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body("ID Token es requerido.");
        }

        try {
            String uid = authService.authenticateToken(idToken);
            return ResponseEntity.ok().body("Autenticación exitosa para UID: " + uid);

        } catch (FirebaseAuthException e) {
            // Error de token inválido, expirado, etc.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Firebase inválido: " + e.getMessage());
        }
    }
}