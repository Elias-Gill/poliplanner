#!/bin/bash
MODULE=$1
echo "🔥 Recompilando $MODULE y dependientes..."
cd $MODULE
mvn clean install -pl . -am -DskipTests

# Si es app, levanta el Spring Boot
if [ "$MODULE" = "app" ]; then
    echo "🚀 Iniciando Spring Boot..."
    cd app
    mvn spring-boot:run
fi
