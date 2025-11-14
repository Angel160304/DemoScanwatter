package com.example.demo.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ===== REGISTRO =====
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


    // ===== LOGIN REAL =====
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
}
