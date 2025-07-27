# ---------- Etapa 1: Build con Maven ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos los archivos necesarios para compilar el proyecto
COPY . .

# Ejecutamos el build (con descarga de dependencias y sin tests)
RUN mvn clean package -DskipTests

# ---------- Etapa 2: Imagen final con solo JRE + gnumeric ----------
FROM eclipse-temurin:17-jre-jammy

# Instala gnumeric
RUN apt-get update && \
    apt-get install -y --no-install-recommends gnumeric && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Crea el directorio de trabajo
WORKDIR /app

# Copia el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot
ENV PORT 8080
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
