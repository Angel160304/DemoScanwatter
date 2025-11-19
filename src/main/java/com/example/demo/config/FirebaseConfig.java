@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            // Ruta donde Render guarda los Secret Files
            InputStream serviceAccount = new FileInputStream("/etc/secrets/scanwatter-1bf04-firebase-adminsdk-fbsvc-06b9d41476.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase inicializado correctamente en Render");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error inicializando Firebase: " + e.getMessage());
        }
    }
}
