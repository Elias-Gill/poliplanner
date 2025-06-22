test:
	mvn test -Dgroups=unit

test-integration:
	mvn test -Dgroups=integration

test-full:
	mvn test -Dgroups=unit,integration

run:
	mvn spring-boot:run

# Carga los datos semilla iniciales para el ambiente de desarrollo
seed:
	mvn spring-boot:run -Dspring-boot.run.arguments=--seed

format: 
	find . -name "*.java" -print0 | xargs -0 google-java-format --aosp -i
