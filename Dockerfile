# ---------- Etapa de Build (Alpine para minimizar tamaño) ----------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /poliplanner

# 1. Copiar todos los POMs primero para cachear dependencias
COPY pom.xml .
COPY app/pom.xml app/
COPY service/pom.xml service/
COPY persistence/pom.xml persistence/
COPY excel/pom.xml excel/
COPY web/pom.xml web/
COPY exception/pom.xml exception/

# Descargar dependencias offline
RUN mvn dependency:go-offline -B

# 2. Copiar todo el código fuente y compilar
COPY . .
RUN mvn package -DskipTests -T 1C -Dmaven.compiler.showWarnings=false

# ---------- Etapa de Producción (Debian Slim para Gnumeric) ----------
FROM eclipse-temurin:17-jre-jammy

# Crear usuario y configurar workspace
RUN useradd -ms /bin/bash springuser
WORKDIR /poliplanner
USER springuser

# Copiar JAR generado desde la etapa de build
COPY --from=build --chown=springuser:springuser /poliplanner/app/target/*.jar ./app.jar

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

# ENTRYPOINT seguro usando array (para que JAVA_OPTS funcione se puede usar CMD)
ENTRYPOINT ["java", "-jar", "-noverify", "/poliplanner/app.jar", "--spring.profiles.active=prod"]
