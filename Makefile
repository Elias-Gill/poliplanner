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

clean-seed:
	mvn spring-boot:run -Dspring-boot.run.arguments="--clean --seed"

format: 
	mvn spotless:apply

lint: 
	mvn spotless:check

fly-deploy:
	mvn clean package
	fly deploy
