#!/bin/bash

# Configuración
PROJECT_ROOT=$(pwd)
MODULES=("exception" "persistence" "service" "excel" "web" "app") # Tus módulos
THREADS=1C # Usar todos los cores (1 por core)

# Colores para formato
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para ejecutar tests en un módulo
function run_module_tests() {
    local module=$1
    cd "$PROJECT_ROOT/$module"

    echo -e "${YELLOW}⚙️ Ejecutando tests en $module...${NC}"

    # Comando base
    local command="mvn test -T $THREADS -DskipITs=$SKIP_INTEGRATION_TESTS"

    # Ejecución con manejo de errores
    if eval "$command"; then
        echo -e "${GREEN}✅ Tests en $module pasaron${NC}"
        return 0
    else
        echo -e "${RED}❌ Fallaron tests en $module${NC}"
        return 1
    fi
}

# Ejecución principal
echo -e "${YELLOW}🚀 Iniciando ejecución de tests por módulo...${NC}"
echo "----------------------------------------"

failed_modules=()

for module in "${MODULES[@]}"; do
    if [ -d "$PROJECT_ROOT/$module" ]; then
        if ! run_module_tests "$module"; then
            failed_modules+=("$module")
        fi
    else
        echo -e "${RED}⚠️ Módulo $module no encontrado${NC}"
    fi
    echo "----------------------------------------"
done

# Resumen final
if [ ${#failed_modules[@]} -eq 0 ]; then
    echo -e "${GREEN}🎉 Todos los tests pasaron correctamente${NC}"
else
    echo -e "${RED}💥 Fallaron tests en los módulos: ${failed_modules[*]}${NC}"
    exit 1
fi
