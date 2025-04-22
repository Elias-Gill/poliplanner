test:
	mvn test -Dgroups=unit

integration-test:
	mvn test -Dgroups=integration

run:
	mvn spring-boot:run
