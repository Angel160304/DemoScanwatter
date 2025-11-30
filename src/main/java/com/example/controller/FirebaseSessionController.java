// Archivo: com.example.controller.FirebaseSessionController.java

package com.example.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// Si tu versión de Spring Boot usa javax.servlet en lugar de jakarta, usa:
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class FirebaseSessionController {

    @PostMapping("/firebase")
    public ResponseEntity<String> createSession(@RequestBody TokenRequest tokenRequest, HttpServletRequest request) {
        String idToken = tokenRequest.getToken();
        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }
        
        try {
            // 1. Validar el token con Firebase Admin SDK
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = firebaseToken.getUid();
            
            // 2. Crear el objeto de autenticación de Spring Security
            // Usamos el UID de Firebase como nombre de usuario principal.
            User springUser = new User(uid, "", java.util.Collections.emptyList());
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities());

            // 3. Crear la Sesión de Spring Security y guardar el contexto
            HttpSession session = request.getSession(true);
            
            // Establecer el contexto de seguridad en la sesión
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                                 SecurityContextHolder.getContext());

            return ResponseEntity.ok("Session created for UID: " + uid);

        } catch (Exception e) {
            System.err.println("Firebase Auth Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or expired.");
        }
    }

    // Clase auxiliar para recibir el JSON del frontend
    public static class TokenRequest {
        private String token;
        // Getters y Setters son requeridos por Jackson (Spring) para deserializar el JSON
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}