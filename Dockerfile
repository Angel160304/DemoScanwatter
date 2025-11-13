# Usamos una imagen oficial de Java 17 ligera
FROM openjdk:17-jdk-slim

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el jar compilado (asegúrate de compilarlo antes)
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la app
ENTRYPOINT ["java","-jar","app.jar"]
