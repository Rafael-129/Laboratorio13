# Usa una imagen de Java 17 (compatible con Spring Boot 3+)
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copia el jar generado al contenedor
COPY target/Laboratorio13-*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
