# syntax=docker/dockerfile:1

############
# Build stage
############
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1) Cachea dependencias
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

# 2) Compila
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests clean package

#############
# Run stage
#############
FROM eclipse-temurin:21-jre
WORKDIR /app

# Usuario no root
RUN useradd -ms /bin/bash appuser
USER appuser

# Copia el jar construido (si tu jar no termina en SNAPSHOT, usa *.jar)
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

# Opcionales útiles
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -Duser.timezone=America/Bogota -Dfile.encoding=UTF-8" \
    SPRING_PROFILES_ACTIVE=docker

# Puerto del servicio (según tu application.yml)
EXPOSE 8082

# Ejecuta
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
