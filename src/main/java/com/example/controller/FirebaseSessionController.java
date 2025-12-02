package com.example.demo.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;

@RestController
@RequestMapping("/api/login")
public class FirebaseSessionController {

    private final FirebaseAuth firebaseAuth;

    @Autowired
    public FirebaseSessionController(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // Clase interna simple para manejar la entrada del JSON
    private static class LoginRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    /**
     * Endpoint que recibe el ID Token de Firebase y crea la sesión de Spring Security.
     * @param loginRequest Objeto que contiene el token JWT de Firebase.
     * @param request Objeto HttpServletRequest para manejar la sesión.
     * @return Respuesta HTTP de éxito o error.
     */
    @PostMapping("/firebase")
    public ResponseEntity<?> createSession(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        if (loginRequest == null || loginRequest.getIdToken() == null) {
            return ResponseEntity.badRequest().body("Token ID is required.");
        }

        try {
            // 1. Validar el token JWT de Firebase
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getIdToken());
            String uid = decodedToken.getUid();

            // 2. 🛑 CRÍTICO: Invalidar cualquier sesión HTTP previa para evitar conflictos de seguridad (403).
            HttpSession session = request.getSession(false); 
            if (session != null) {
                session.invalidate(); 
            }

            // 3. Crear el nuevo contexto de seguridad
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                uid, 
                null, // No se necesita la contraseña aquí
                Collections.emptyList() // Asignar roles vacíos por defecto
            );
            
            // 4. Establecer el contexto de seguridad actual
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 5. Crear la nueva sesión HTTP y guardar el contexto de seguridad
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                SecurityContextHolder.getContext()
            );

            // Opcional: Establecer un tiempo de vida (ej: 1 hora)
            newSession.setMaxInactiveInterval(60 * 60); 

            return ResponseEntity.ok().body("{\"message\": \"Session established successfully\", \"uid\": \"" + uid + "\"}");

        } catch (FirebaseAuthException e) {
            // Error si el token es inválido o expiró
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            // Error del servidor (ej: si falla la inyección de FirebaseAuth)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }
}