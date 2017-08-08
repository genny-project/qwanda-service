#!/bin/bash
     export KEYCLOAK_URL=http://localhost:8180
     export KEYCLOAK_REALM=wildfly-swarm-keycloak-example
     export KEYCLOAK_USERNAME=user1
     export KEYCLOAK_PASSWORD=password1
     export KEYCLOAK_CLIENTID=curl
     export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5000"
     export KEYCLOAK_SECRET=6c61c6c3-aa68-419a-86e0-750a2517a3b0
     export MYSQL_URL=127.0.0.1
     export MYSQL_DB=gennydb
     export MYSQL_PORT=3310
     export MYSQL_USER=genny
     export MYSQL_PASSWORD=password

#java  -Xms256m -Xmx512m -XX:MaxPermSize=256m   -jar target/qwanda-service-swarm.jar
java  -Xms256m -Xmx512m -XX:MaxPermSize=256m   -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y    -jar target/qwanda-service-swarm.jar
