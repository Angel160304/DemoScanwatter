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
        
        // 🛑 CRÍTICO: Imprimir la ruta que Spring está intentando usar (solo para logs de Render)
        System.out.println("DEBUG: Intentando cargar clave Firebase desde la ruta: " + firebaseSdkPath);

        try (InputStream serviceAccount = new FileInputStream(firebaseSdkPath)) {
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("DEBUG: Firebase SDK inicializado exitosamente.");
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();

        } catch (IOException e) {
            // 🛑 MENSAJE DE ERROR MEJORADO: Indica claramente que la lectura del archivo falló
            System.err.println("❌ CRÍTICO: Falla al leer el archivo secreto de Firebase.");
            throw new IOException("Falla al inicializar Firebase Admin SDK. No se encontró el archivo: " + firebaseSdkPath + ". Razón original: " + e.getMessage(), e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp); 
    }
}