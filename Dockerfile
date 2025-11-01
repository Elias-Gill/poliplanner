# ---------- Etapa de Build ----------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /poliplanner

# Copiar solo POMs para cachear dependencias
COPY pom.xml .
COPY app/pom.xml app/
COPY service/pom.xml service/
COPY persistence/pom.xml persistence/
COPY excel/pom.xml excel/
COPY web/pom.xml web/
RUN mvn dependency:go-offline -B

# Copiar código y compilar solo el módulo app
COPY . .
RUN mvn clean package -DskipTests -pl app -am

# Explotar el JAR en la etapa de build y verificar contenido
RUN mkdir exploded && cd exploded && jar -xf ../app/target/*.jar && ls -R .

# ---------- Etapa de Producción ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /poliplanner

# Crear usuario no root
RUN adduser -D springuser
# Darle permisos a springuser en /poliplanner
RUN chown -R springuser:springuser /poliplanner
# Cambiar a usuario no root
USER springuser

# Copiar el JAR ya explotado desde la etapa de build
COPY --from=build --chown=springuser:springuser /poliplanner/exploded .

# Verificar contenido copiado (para depuración)
RUN ls -R BOOT-INF/classes/poliplanner

# Variables de memoria optimizadas para arranque rápido
ENV JAVA_OPTS="\
-XX:+UseSerialGC \                    # GC más simple, menos memoria
-XX:+UseStringDeduplication \
-Xss128k \                           # Stack size reducido
-Xmx200m \                           # Heap máximo reducido
-Xms50m \                            # Heap inicial mínimo
-XX:MaxMetaspaceSize=40m \           # Metaspace reducido
-XX:MaxRAM=250m \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseEpsilonGC \                  # O usar Epsilon GC si no hay allocation
-XX:+AlwaysPreTouch \                # Pre-touch memory
-Dspring.jmx.enabled=false \
-Djava.awt.headless=true \
-Dfile.encoding=UTF-8"

EXPOSE 8080

# Usar la clase principal con un classpath explícito
ENTRYPOINT ["sh", "-c", \
"java $JAVA_OPTS -cp BOOT-INF/classes:BOOT-INF/lib/* poliplanner.PoliPlanner --spring.profiles.active=prod --spring.main.lazy-initialization=true"]
