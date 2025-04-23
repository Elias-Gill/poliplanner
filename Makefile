test:
	mvn test -Dgroups=unit

test-integration:
	mvn test -Dgroups=integration

test-full:
	mvn test -Dgroups=unit,integration

run:
	mvn spring-boot:run
