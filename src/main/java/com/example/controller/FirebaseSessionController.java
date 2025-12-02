// Archivo: com.example.demo.controller.FirebaseSessionController.java

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
import java.util.Map;

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
     * @param loginRequest Objeto que contiene el token JWT de Firebase.
     * @return Respuesta HTTP de éxito o error (siempre en formato JSON).
     */
    @PostMapping("/firebase") // ⬅️ RUTA CORRECTA: /api/login/firebase
    public ResponseEntity<?> createSession(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getIdToken() == null) {
            // Error 400 (Bad Request) - Siempre devuelve JSON
            return ResponseEntity.badRequest().body(Map.of("error", "Token ID is required."));
        }

        try {
            // 1. Validar el token JWT de Firebase
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getIdToken());
            String uid = decodedToken.getUid();
            
            // 2. Retorna JSON de éxito
            return ResponseEntity.ok().body(Map.of("message", "Token validated successfully", "uid", uid));

        } catch (FirebaseAuthException e) {
            // Error 401 (Unauthorized) - Token inválido o expirado. Siempre devuelve JSON
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Invalid Firebase token",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            // Error 500 (Server error). Siempre devuelve JSON
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Server error during token validation",
                "message", e.getMessage()
            ));
        }
    }
}