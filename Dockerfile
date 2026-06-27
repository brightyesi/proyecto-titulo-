# Etapa 1: Compilación automática dentro del contenedor usando Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el archivo de configuración de Maven
COPY pom.xml .

# Copiamos todo tu código por capas
COPY src ./src

# Compilamos y empaquetamos el archivo .jar automáticamente
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ultra ligera (la que tú tenías) para ejecutar el sistema
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tomamos el .jar recién generado en la Etapa 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]