package com.example.demo.controller;

import com.example.demo.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private FirebaseAuthService authService;

    // ===== Registro de usuario =====
    // Puedes registrar como USER (por defecto) o ADMIN
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email,
                                           @RequestParam String password,
                                           @RequestParam(defaultValue = "USER") String role) {
        try {
            // Crear usuario en Firebase
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // Asignar rol (custom claim)
            Map<String, Object> claims = new HashMap<>();

            // ✅ Si es el UID que me pasaste, asignar ADMIN automáticamente
            if (userRecord.getUid().equals("7NdfxdEsU9cSfuHH8cWWthcwGE03")) {
                claims.put("role", "ADMIN");
            } else {
                claims.put("role", role.toUpperCase()); // "USER" o lo que pases
            }

            FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);

            return ResponseEntity.ok("Usuario creado con UID: " + userRecord.getUid() + " y rol: " + claims.get("role"));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear usuario: " + e.getMessage());
        }
    }

    // ===== Verificar token de Firebase =====
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Firebase inválido: " + e.getMessage());
        }
    }

    // ===== Asignar rol ADMIN a cualquier usuario existente =====
    @PostMapping("/make-admin")
    public ResponseEntity<String> makeAdmin(@RequestParam String uid) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "ADMIN");
            FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
            return ResponseEntity.ok("Usuario " + uid + " ahora es ADMIN ✅");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(500).body("Error al asignar rol: " + e.getMessage());
        }
    }
}
