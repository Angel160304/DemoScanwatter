// Archivo: com.example.demo.controller.StatusController.java

package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    /**
     * Endpoint protegido para verificar que el FirebaseTokenFilter pasó la autenticación.
     */
    @GetMapping("/api/check")
    public ResponseEntity<?> checkStatus() {
        // Si llegamos a esta línea, Spring Security ya autenticó el token.
        return ResponseEntity.ok().build(); 
    }
}