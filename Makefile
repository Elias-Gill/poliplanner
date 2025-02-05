clean:
	mvn clean

run:
	mvn package -Dmaven.test.skip
	java -jar ./target/poliplanner-1.0-SNAPSHOT.jar

rerun:
	java -jar ./target/poliplanner-1.0-SNAPSHOT.jar
