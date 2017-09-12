#!/bin/bash
#Warning - for osx
export REBEL_BASE=~/.jrebel
export REBEL_HOME=~/.p2/pool/plugins/org.zeroturnaround.eclipse.embedder_7.0.8.RELEASE/jrebel
export KEYCLOAK_PORT=8180
export KEYCLOAK_PROTO="http://"
export KEYCLOAK_USERNAME=user1
export KEYCLOAK_SERVICE_ID=3f0c17d2-36f4-4b0d-8204-effadaedf4f2
export KEYCLOAK_PASSWORD=password1
export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5000"
export KEYCLOAK_SECRET=6c61c6c3-aa68-419a-86e0-750a2517a3b0


export MYSQL_URL=127.0.0.1
export MYSQL_DB=gennydb
export MYSQL_PORT=3310
export MYSQL_USER=genny
export MYSQL_PASSWORD=password
export MYSQL_ROOT_PASSWORD=password
java  -Xms256m -Xmx512m -XX:MaxPermSize=256m   -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y -Dswarm.keycloak.json.path=./realm/keycloak.json -jar target/qwanda-service-swarm.jar
#java -agentpath:$REBEL_HOME/lib/libjrebel64.dylib -Xms256m -Xmx512m -XX:MaxPermSize=256m   -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y -jar target/qwanda-keycloak-swarm.jar

