test:
	mvn test

integration-test:
	mvn test -Dspring.profiles.active=integracion

run:
	mvn spring-boot:run
