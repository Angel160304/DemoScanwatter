package com.example.demo.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * Endpoint que recibe el ID Token de Firebase y lo valida.
     * No crea una sesión HTTP, solo confirma la validez del token (Stateless).
     * @param loginRequest Objeto que contiene el token JWT de Firebase.
     * @return Respuesta HTTP de éxito o error.
     */
    @PostMapping("/firebase")
    public ResponseEntity<?> createSession(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getIdToken() == null) {
            return ResponseEntity.badRequest().body("Token ID is required.");
        }

        try {
            // 1. Validar el token JWT de Firebase
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getIdToken());
            String uid = decodedToken.getUid();
            
            // 2. No se crea la sesión de Spring Security. El frontend ahora usará este token 
            //    para autenticar TODAS las demás solicitudes mediante un filtro (Token Filter).

            // Retorna un mensaje de éxito para que el frontend sepa que el token es válido
            return ResponseEntity.ok().body("{\"message\": \"Token validated successfully\", \"uid\": \"" + uid + "\"}");

        } catch (FirebaseAuthException e) {
            // Error si el token es inválido o expiró
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            // Error del servidor 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }
}