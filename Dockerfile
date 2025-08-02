# ---------- Etapa de Build (Alpine para minimizar tamaño) ----------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# 1. Copia solo el POM para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copia el código y compila
COPY src src
RUN mvn package -DskipTests -T 1C -Dmaven.compiler.showWarnings=false

# ---------- Etapa de Producción (Debian Slim para Gnumeric) ----------
FROM eclipse-temurin:17-jre-jammy

# Instalación mínima de Gnumeric (elimina cache después)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    gnumeric \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Configuración de seguridad y espacio
RUN useradd -ms /bin/bash springuser
USER springuser
WORKDIR /app

# Copia el JAR desde la etapa de build
COPY --from=build --chown=springuser:springuser /app/target/*.jar app.jar

# Variables críticas para 300MB RAM
ENV JAVA_OPTS="\
    -XX:+UseSerialGC \
    -Xss256k \
    -Xmx350m \
    -Xms100m \
    -XX:MaxRAM=380m \
    -Djava.awt.headless=true \
    -Dfile.encoding=UTF-8"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=prod"]
