# ===== Stage 1: Build =====
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Directorio de trabajo
WORKDIR /app

# Copiamos pom.xml primero para aprovechar cache
COPY pom.xml .

# Copiamos todo el código fuente
COPY src ./src

# Construimos el JAR (omitimos tests para acelerar)
RUN mvn clean package -DskipTests

# ===== Stage 2: Run =====
FROM eclipse-temurin:17-jdk

# Directorio de trabajo en el contenedor final
WORKDIR /app

# Copiamos el JAR compilado desde el stage de build
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
