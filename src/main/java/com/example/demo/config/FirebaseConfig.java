// Archivo: com.example.demo.config.FirebaseConfig.java

package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig { 

    @Value("${firebase.sdk.path}") 
    private String firebaseSdkPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        
        // Mensaje de diagnóstico para el log de Render
        System.out.println("DEBUG: Intentando cargar clave Firebase desde la ruta: " + firebaseSdkPath);

        // Usamos try-with-resources para asegurar que el stream se cierre
        try (InputStream serviceAccount = new FileInputStream(firebaseSdkPath)) {
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                // ❌ SE ELIMINA: .setHttpTimeout(30000) (Causaba el error de compilación)
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("DEBUG: Firebase SDK inicializado exitosamente.");
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();

        } catch (IOException e) {
            // Error CRÍTICO si el archivo no se encuentra o no se puede leer
            System.err.println("❌ CRÍTICO: Falla al leer el archivo secreto de Firebase.");
            throw new IOException("Falla al inicializar Firebase Admin SDK. No se encontró el archivo: " + firebaseSdkPath + ". Razón original: " + e.getMessage(), e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        // Obtenemos la instancia de FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance(firebaseApp);
        
        // ❌ SE ELIMINA: auth.getClient().setTimeTolerance(300) (Causaba el error de compilación)
        
        System.out.println("DEBUG: FirebaseAuth inicializado.");
        return auth; 
    }
}