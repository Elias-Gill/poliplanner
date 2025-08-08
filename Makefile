# Variables
MODULES = app web service persistence excel exception
APP_DIR = app
MVN = mvn
FLY_CMD = flyctl

# Targets principales
.PHONY: test test-unit test-integration test-full run seed clean-seed format lint

# Testing
test-unit:
	@echo "Ejecutando tests unitarios..."
	cd $(APP_DIR) && $(MVN) test -Dgroups=unit

test-integration:
	@echo "Ejecutando tests de integración..."
	cd $(APP_DIR) && $(MVN) test -Dgroups=integration

test-full:
	@echo "Ejecutando todos los tests..."
	cd $(APP_DIR) && $(MVN) test -Dgroups=unit,integration

# Ejecución de la aplicación
run:
	@echo "Iniciando aplicación Spring Boot..."
	cd $(APP_DIR) && $(MVN) spring-boot:run

seed:
	@echo "Ejecutando seeding de datos..."
	cd $(APP_DIR) && $(MVN) spring-boot:run -Dspring-boot.run.arguments=--seed

clean-seed:
	@echo "Limpiando y recreando datos..."
	cd $(APP_DIR) && $(MVN) spring-boot:run -Dspring-boot.run.arguments="--clean --seed"

# Formateo y linting
format:
	@echo "Aplicando formato al código..."
	@for dir in $(MODULES); do \
		echo "Formateando $$dir..."; \
		cd $$dir && $(MVN) spotless:apply && cd .. || exit 1; \
	done

lint:
	@echo "Verificando formato del código..."
	@for dir in $(MODULES); do \
		echo "Linting $$dir..."; \
		cd $$dir && $(MVN) spotless:check && cd .. || exit 1; \
	done

# Ayuda
help:
	@echo "Makefile para PoliPlanner"
	@echo ""
	@echo "Targets disponibles:"
	@echo "  clean         Limpia todos los módulos"
	@echo "  test-unit     Ejecuta tests unitarios"
	@echo "  test-integration Ejecuta tests de integración"
	@echo "  test-full     Ejecuta todos los tests"
	@echo "  run           Inicia la aplicación"
	@echo "  seed          Ejecuta seeding de datos"
	@echo "  clean-seed    Limpia y recrea datos"
	@echo "  format        Aplica formato al código"
	@echo "  lint          Verifica formato del código"
