# 1. Build con JDK estándar
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY app/pom.xml app/
COPY service/pom.xml service/
COPY persistence/pom.xml persistence/
COPY excel/pom.xml excel/
COPY web/pom.xml web/

RUN mvn dependency:go-offline -B
COPY . .

RUN mvn -DskipTests package -pl app -am

# 2. Crear binario nativo
FROM ghcr.io/graalvm/native-image:21 AS native
WORKDIR /app
COPY --from=build /app/app/target/*.jar app.jar

ENV USE_CONTAINER_SUPPORT=false
ENV JAVA_TOOL_OPTIONS="-XX:-UseContainerSupport"

RUN native-image -H:-UseContainerSupport --no-fallback -jar app.jar poliplanner

# 3. Imagen final
FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=native /app/poliplanner .
USER 1000
EXPOSE 8080
ENTRYPOINT ["./poliplanner"]
