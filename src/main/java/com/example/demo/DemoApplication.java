package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct; // Importante para inicializar después del arranque
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Método que se ejecuta justo después de que Spring haya cargado la aplicación
    @PostConstruct
    public void initializeFirebase() {
        // La clave de entorno que CONFIGURARÁS en la web de Render
        final String ENV_VAR_NAME = "FIREBASE_SERVICE_ACCOUNT_JSON"; 
        String serviceAccountJson = System.getenv(ENV_VAR_NAME);

        if (serviceAccountJson != null && !serviceAccountJson.isEmpty()) {
            try {
                // Convierte el String JSON (leído de la variable de entorno) a un InputStream
                InputStream serviceAccount = new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
                
                FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                
                // Inicializa la app solo si no se ha hecho antes
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("--- Firebase: Inicializado correctamente desde Variable de Entorno ---");
                }
            } catch (IOException e) {
                System.err.println("--- ERROR FATAL: No se pudo leer el JSON de credenciales ---");
                // Si esto falla, la aplicación no podrá acceder a Firebase
                e.printStackTrace(); 
            }
        } else {
            System.err.println("--- ADVERTENCIA: La variable de entorno " + ENV_VAR_NAME + " no está configurada. Fallará la conexión en Render. ---");
        }
    }
}