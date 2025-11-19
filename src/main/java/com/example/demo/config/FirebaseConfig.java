package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount;

            // Ruta del Secret File en Render
            String ruta = "/etc/secrets/scanwatter-1bf04-firebase-adminsdk-fbsvc-06b9d41476.json";
            File secretFile = new File(ruta);

            if (secretFile.exists()) {
                serviceAccount = new FileInputStream(secretFile);
                System.out.println("📡 Usando clave Firebase desde Render Secret File");
            } else {
                // Si estás ejecutando local
                serviceAccount = new ClassPathResource("firebase-key.json").getInputStream();
                System.out.println("💻 Usando clave Firebase local desde resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase inicializado correctamente");
            }

        } catch (Exception e) {
            System.out.println("❌ Error inicializando Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
