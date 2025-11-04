#!/bin/bash
# run-native.sh – Levanta PoliPlanner Native (Docker + GraalVM)
set -euo pipefail

# === CONFIG ===
IMAGE_NAME="poliplanner-native"
CONTAINER_NAME="poliplanner-test"
PORT=8080
DOCKERFILE="Dockerfile"
ENV_FILE=".env.local"
EXAMPLE_ENV="example.env"

# === COLORES ===
G='\033[1;32m'; Y='\033[1;33m'; R='\033[1;31m'; N='\033[0m'
log()   { echo -e "${G}[OK]${N} $1"; }
warn()  { echo -e "${Y}[!]${N} $1"; }
error() { echo -e "${R}[ERROR]${N} $1"; exit 1; }

# === 1. REBUILD O REUSAR IMAGEN ===
if [[ "$*" == *"--rebuild"* ]] || ! docker image inspect "$IMAGE_NAME" &>/dev/null; then
    log "Construyendo $IMAGE_NAME (5-15 min)..."
    docker build -f "$DOCKERFILE" -t "$IMAGE_NAME" . || error "FALLÓ EL BUILD"
else
    log "Imagen $IMAGE_NAME lista. Usa --rebuild para forzar."
fi

# === 2. LIMPIAR CONTENEDOR VIEJO ===
if docker ps -q -f name="$CONTAINER_NAME" &>/dev/null; then
    warn "Parando $CONTAINER_NAME..."
    docker stop "$CONTAINER_NAME" >/dev/null
fi
if docker ps -aq -f name="$CONTAINER_NAME" &>/dev/null; then
    docker rm "$CONTAINER_NAME" >/dev/null
fi

# === 3. .env.local ===
[ ! -f "$ENV_FILE" ] && [ -f "$EXAMPLE_ENV" ] && {
    cp "$EXAMPLE_ENV" "$ENV_FILE"
    warn "$ENV_FILE creado. EDITALO con tus claves."
}

# === 4. CORRER ===
log "Iniciando en http://localhost:$PORT"
docker run -d \
    --name "$CONTAINER_NAME" \
    --env-file "$ENV_FILE" \
    -p "$PORT:$PORT" \
    "$IMAGE_NAME" || error "NO ARRANCA EL CONTENEDOR"

# === 5. ESPERAR + HEALTH ===
log "Esperando arranque..."
sleep 2

for i in {1..10}; do
    if curl -s "http://localhost:$PORT/actuator/health" | grep -q '"status":"UP"'; then
        log "¡CORRIENDO! → http://localhost:$PORT"
        log "Health: curl http://localhost:$PORT/actuator/health"
        log "Logs:   docker logs -f $CONTAINER_NAME"
        log "Parar:  docker stop $CONTAINER_NAME"
        exit 0
    fi
    printf "."
    sleep 1
done

warn "Health falló. Logs:"
docker logs "$CONTAINER_NAME" | tail -30
error "NO responde. Revisa DB, .env.local o reflection."
