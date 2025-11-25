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

    // ===== LOGIN =====
    public boolean loginUsuario(String email, String password) throws Exception {
        UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
        if (user == null) return false;

        String uid = user.getUid();
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("users").document(uid).get().get();
        if (!doc.exists()) return false;

        String storedHash = doc.getString("passwordHash");
        return passwordEncoder.matches(password, storedHash);
    }

    /*
    // ===== AUTENTICACIÓN CON TOKEN =====
    // Comentado para desactivar la validación de token y evitar bucles de redirección

    public String authenticateToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(uid, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return uid;
    }
    */
}
