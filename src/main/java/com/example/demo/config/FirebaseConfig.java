// Archivo: com.example.demo.config.FirebaseConfig.java (CORREGIDO)

package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig { 
    
    // Ya no necesitamos @Value("${firebase.sdk.path}")

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        
        final String ENV_VAR_NAME = "FIREBASE_SERVICE_ACCOUNT_JSON";
        String serviceAccountJson = System.getenv(ENV_VAR_NAME);

        if (serviceAccountJson == null || serviceAccountJson.isEmpty()) {
            throw new IOException("Falla al inicializar Firebase Admin SDK. La variable " + ENV_VAR_NAME + " no está configurada.");
        }
        
        // 🛑 CRÍTICO: Lee el JSON de la variable de entorno y lo convierte a InputStream
        InputStream serviceAccount = new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
            
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
            
        if (FirebaseApp.getApps().isEmpty()) {
            System.out.println("DEBUG: Firebase SDK inicializado exitosamente (desde ENV VAR).");
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();

    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        FirebaseAuth auth = FirebaseAuth.getInstance(firebaseApp);
        System.out.println("DEBUG: FirebaseAuth inicializado.");
        return auth; 
    }
}