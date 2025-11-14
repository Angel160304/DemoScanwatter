package com.example.demo.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseAuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==== REGISTRO ====
    public String registrarUsuario(String email, String password) throws Exception {

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        String hash = passwordEncoder.encode(password);

        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("uid", uid);
        data.put("passwordHash", hash);
        data.put("createdAt", new java.util.Date());

        db.collection("users").document(uid).set(data).get();

        return uid;
    }

    // ==== LOGIN ====
    public boolean loginUsuario(String email, String password) throws InterruptedException, ExecutionException {

        Firestore db = FirestoreClient.getFirestore();

        // Buscar por email
        QuerySnapshot snapshot = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        if (snapshot.isEmpty()) {
            return false; // usuario NO encontrado
        }

        QueryDocumentSnapshot userDoc = snapshot.getDocuments().get(0);

        String storedHash = userDoc.getString("passwordHash");

        if (storedHash == null) {
            return false;
        }

        // Validar password con BCrypt
        return passwordEncoder.matches(password, storedHash);
    }
}
