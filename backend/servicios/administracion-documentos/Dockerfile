# Usa una imagen de Maven para compilar la aplicación
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Crea un directorio para el código
WORKDIR /app

# Copia el pom y descarga dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el código fuente
COPY src ./src

# Compila el proyecto y genera el .jar
RUN mvn clean package -DskipTests

# Etapa final: usar una imagen liviana con JDK
FROM eclipse-temurin:21-jdk

# Crear carpeta para app
WORKDIR /app

# Copiar el jar compilado
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto (ajusta según tu aplicación)
EXPOSE 8081

# Comando para correr la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
