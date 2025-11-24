package com.example.demo.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken; 
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 💡 Nueva Importación

import java.util.ArrayList; // 💡 Nueva Importación
import java.util.Collections;
import java.util.HashMap;
import java.util.List; // 💡 Nueva Importación
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseAuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        // Guardar usuario + hash + ROL POR DEFECTO en Firestore
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("uid", uid);
        data.put("passwordHash", hash);
        data.put("createdAt", new java.util.Date());
        data.put("role", "user"); // 💡 ROL POR DEFECTO
        
        db.collection("users").document(uid).set(data).get();

        return uid;
    }


    // ===== LOGIN REAL (Lógica existente - Mantenida por consistencia) =====
    public boolean loginUsuario(String email, String password) throws Exception {
        // ... (Tu lógica de login con BCrypt) ...
        // Este método realmente solo verifica la contraseña con BCrypt
        // y se ejecuta ANTES de que el cliente obtenga el idToken.
        
        UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
        if (user == null) { return false; }
        String uid = user.getUid();
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("users").document(uid).get().get();

        if (!doc.exists()) { return false; }
        String storedHash = doc.getString("passwordHash");

        return passwordEncoder.matches(password, storedHash);
    }
    
    
    // ===== 🔑 MÉTODO CRÍTICO PARA SPRING SECURITY (MODIFICADO) =====
    public String authenticateToken(String idToken) throws FirebaseAuthException {
        // 1. Verificar el token y decodificarlo
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        
        // 2. OBTENER EL ROL DEL CUSTOM CLAIM
        String role = "USER"; // Rol por defecto si no hay claims
        if (decodedToken.getClaims().containsKey("role")) {
            // Aseguramos que el rol sea mayúsculas (ADMIN o USER) para Spring Security
            role = ((String) decodedToken.getClaims().get("role")).toUpperCase(); 
        }

        // 3. Crear las Authorities (Roles) para Spring Security
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // Siempre se añade el prefijo "ROLE_"
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        
        // 4. Autenticar en Spring Security con el rol
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(uid, null, authorities);
        
        // 5. Establecer la autenticación en el contexto de seguridad.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return uid;
    }
    
    // 💡 Método para asignar el rol de administrador (solo para tu uso interno y seguro)
    public void setAdminRole(String uid) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin"); // El claim se establece en minúsculas
        firebaseAuth.setCustomUserClaims(uid, claims);
        
        // Nota: Es posible que debas actualizar el documento en Firestore si lo usas
        // como fuente de verdad para el rol, pero el claim es la fuente oficial.
    }
}