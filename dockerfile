# Etapa 1: Compilación
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom y las dependencias primero (para aprovechar caché)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el resto del proyecto
COPY src ./src

# Compilamos el JAR sin ejecutar tests
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el jar generado desde la etapa anterior
COPY --from=build /app/target/agricola-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
