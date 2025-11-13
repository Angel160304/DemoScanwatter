package com.example.demo.service;

import com.google.cloud.firestore.Firestore;
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

    // Registrar usuario, generar hash y guardar en Firestore
    public String registrarUsuario(String email, String password) throws Exception {
        // Crear usuario en Firebase Auth
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        //Generar hash de la contraseña con BCrypt
        String hash = passwordEncoder.encode(password);

        // Guardar usuario y hash en Firestore
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("uid", uid);
        data.put("passwordHash", hash); // aquí verás el hash en Firestore
        data.put("createdAt", new java.util.Date());

        db.collection("users").document(uid).set(data).get();

        return uid;
    }

    // --- Métodos existentes para autenticación aquí ---

   

    // Guardar lectura en Firestore
    // public void saveWaterMeasurement(WaterMeasurement measurement) {
    //     Firestore db = FirestoreClient.getFirestore();
    //     CollectionReference measurements = db.collection(COLLECTION_NAME);
    //     ApiFuture<com.google.cloud.firestore.DocumentReference> future = measurements.add(measurement);
    //     try {
    //         System.out.println("Documento guardado con ID: " + future.get().getId());
    //     } catch (InterruptedException | ExecutionException e) {
    //         e.printStackTrace();
    //     }
    // }

    // Obtener todas las mediciones
    // public List<WaterMeasurement> getAllWaterMeasurements() {
    //     Firestore db = FirestoreClient.getFirestore();
    //     CollectionReference measurements = db.collection(COLLECTION_NAME);
    //     try {
    //         return measurements.get().get().toObjects(WaterMeasurement.class);
    //     } catch (InterruptedException | ExecutionException e) {
    //         e.printStackTrace();
    //         return List.of();
    //     }
    // }
}
