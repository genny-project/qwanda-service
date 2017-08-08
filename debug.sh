#!/bin/bash
java -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y -jar target/qwanda-keycloak-swarm.jar

