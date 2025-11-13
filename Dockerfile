# 1. IMAGEN BASE: Usamos openjdk:17-jdk, que es más seguro y fácil de encontrar.
# Usar la etiqueta 'jdk' es más fiable que 'jdk-slim' en algunos entornos de despliegue.
FROM openjdk:17-jdk-slim-bullseye

# 2. DIRECTORIO DE TRABAJO
WORKDIR /app

# 3. COPIAR JAR COMPILADO (Se asume que la compilación se hace en Render)
# Nota: Render ejecutará tu comando de compilación (ej: ./mvnw package) antes de ejecutar el Dockerfile.
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 4. ELIMINAR COPIA DE CREDENCIALES
# IMPORTANTE: Se ha eliminado la línea que copia 'firebase-key.json'.
# Las credenciales DEBEN cargarse a través de Variables de Entorno de Render.
# NO COPIAR ARCHIVOS SECRETOS DIRECTAMENTE AL CONTENEDOR.

# 5. COMANDO DE EJECUCIÓN
# Comando para ejecutar la app.
ENTRYPOINT ["java","-jar","app.jar"] 