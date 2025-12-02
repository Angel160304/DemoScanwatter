// Archivo: com.example.controller.FirebaseSessionController.java
// Asegúrate de que las importaciones jakarta.servlet sean correctas para tu versión de Spring Boot.

package com.example.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.User; // ⬅️ IMPORTACIÓN ELIMINADA (Ya no se necesita)
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.Authentication; // ⬅️ Importación requerida para la interfaz Authentication


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class FirebaseSessionController {

    @Autowired 
    private FirebaseAuth firebaseAuth; 

    @PostMapping("/firebase")
    public ResponseEntity<String> createSession(@RequestBody TokenRequest tokenRequest, HttpServletRequest request) {
        String idToken = tokenRequest.getToken();
        
        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }
        
        try {
            // 1. Validar el token con el Bean inyectado
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(idToken); 
            String uid = firebaseToken.getUid();
            
            // 🛑 CRÍTICO: 2. Crear el objeto de autenticación de Spring Security (Forma Simplificada)
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                uid, // Principal: el UID de Firebase
                null, // Credenciales: null
                java.util.Collections.emptyList() // Autoridades/Roles
            );

            // 3. Crear la Sesión de Spring Security y guardar el contexto
            HttpSession session = request.getSession(true);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                                 SecurityContextHolder.getContext());

            return ResponseEntity.ok("Session created for UID: " + uid);

        } catch (Exception e) {
            System.err.println("Firebase Auth Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or expired: " + e.getMessage());
        }
    }

    // Clase auxiliar para recibir el JSON del frontend
    public static class TokenRequest {
        private String token;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}