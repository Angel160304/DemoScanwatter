package com.example.demo.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken; // Importación necesaria para el token
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseAuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Inyectamos FirebaseAuth para la verificación del token (se obtiene de FirebaseConfig)
    @Autowired
    private FirebaseAuth firebaseAuth; 

    // ===== REGISTRO (Lógica existente) =====
    public String registrarUsuario(String email, String password) throws Exception {

        // Crear usuario en Firebase Auth
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        // Generar hash BCRYPT
        String hash = passwordEncoder.encode(password);

        // Guardar usuario + hash en Firestore
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("uid", uid);
        data.put("passwordHash", hash);
        data.put("createdAt", new java.util.Date());

        db.collection("users").document(uid).set(data).get();

        return uid;
    }


    // ===== LOGIN REAL (Lógica existente - Mantenida por consistencia) =====
    public boolean loginUsuario(String email, String password) throws Exception {

        // Buscar usuario por email en Firebase Auth
        UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);

        if (user == null) {
            return false;
        }

        String uid = user.getUid();

        // Obtener hash guardado en Firestore
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("users").document(uid).get().get();

        if (!doc.exists()) {
            return false;
        }

        String storedHash = doc.getString("passwordHash");

        // Validar contraseña usando BCrypt
        return passwordEncoder.matches(password, storedHash);
    }
    
    
    // ===== 🔑 MÉTODO CRÍTICO PARA SPRING SECURITY (NUEVO) =====
    /**
     * Valida el token JWT de Firebase recibido del frontend, y si es válido, 
     * establece la sesión de autenticación en Spring Security.
     * * @param idToken Token JWT recibido del cliente.
     * @return El UID del usuario autenticado.
     * @throws FirebaseAuthException Si el token es inválido o ha expirado.
     */
    public String authenticateToken(String idToken) throws FirebaseAuthException {
        // 1. Verificar el token usando Firebase Admin SDK
        // Esto verifica la firma, la expiración y que sea un token de Firebase válido.
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        
        // 2. Autenticar en Spring Security
        // Creamos un token de autenticación simple. No necesitamos contraseña ya que el token JWT es la prueba.
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(uid, null, Collections.emptyList());
        
        // 3. Establecer la autenticación en el contexto de seguridad.
        // Esto le dice a Spring Security que este usuario (identificado por el UID)
        // ya está logueado y crea la sesión web (JSESSIONID).
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return uid;
    }
}