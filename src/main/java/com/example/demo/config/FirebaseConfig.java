package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig { 

    // 1. Inyectamos la cadena JSON completa (leída desde la variable de entorno de Render)
    @Value("${firebase.credentials.json}")
    private String firebaseCredentialsJson;
    
    // El constructor y ResourceLoader ya no son necesarios si usamos la inyección de cadena.
    // Puedes eliminar el constructor y la declaración de resourceLoader.

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        
        // 2. Convertir la cadena JSON en un flujo de entrada (Input Stream)
        InputStream serviceAccount = new ByteArrayInputStream(
            firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8)
        );
        
        try (serviceAccount) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            
            // Inicializa la aplicación si no existe
            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();
        } 
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp); 
    }
}