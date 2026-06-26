# Usamos una imagen directa de Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usamos el punto para decirle que busque desde la raíz actual del proyecto
COPY ./proyecto-titulo/target/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]