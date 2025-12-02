// Archivo: com.example.controller.FirebaseSessionController.java
// Asegúrate de que las importaciones jakarta.servlet sean correctas para tu versión de Spring Boot.

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
import org.springframework.beans.factory.annotation.Autowired; // ⬅️ AÑADIR

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class FirebaseSessionController {

    // 🛑 CRÍTICO: Inyectar el Bean de FirebaseAuth configurado
    @Autowired 
    private FirebaseAuth firebaseAuth; 

    @PostMapping("/firebase")
    public ResponseEntity<String> createSession(@RequestBody TokenRequest tokenRequest, HttpServletRequest request) {
        String idToken = tokenRequest.getToken();
        // ... (resto de la lógica) ...
        
        try {
            // 🛑 CRÍTICO: Usar el objeto 'firebaseAuth' inyectado
            // Antes estaba: FirebaseAuth.getInstance().verifyIdToken(idToken);
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(idToken); 
            String uid = firebaseToken.getUid();
            
            // ... (resto de la lógica de creación de sesión de Spring) ...
            User springUser = new User(uid, "", java.util.Collections.emptyList());
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities());

            HttpSession session = request.getSession(true);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                                 SecurityContextHolder.getContext());

            return ResponseEntity.ok("Session created for UID: " + uid);

        } catch (Exception e) {
            System.err.println("Firebase Auth Error: " + e.getMessage());
            // Se usa UNAUTHORIZED (401) en lugar de Forbidden (403)
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