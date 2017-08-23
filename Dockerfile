FROM openjdk:9-slim 
#FROM openjdk:8u141-slim 

RUN set -x \
    && apt-get update --quiet \
    && apt-get install --quiet --yes --no-install-recommends jq sed  iputils-ping vim sed \
    && apt-get clean

#RUN apk update \
# && apk add jq \
# && apk add sed \
# && apk add vim \
# && apk add bash \
# && rm -rf /var/cache/apk/*

ADD target/qwanda-service-swarm.jar /qwanda-service-swarm.jar
#ADD install-cert.sh /install-cert.sh
ADD ssl.crt /ssl.crt
RUN keytool -importcert -file ./ssl.crt -alias ssl -keystore /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -storepass changeit 
#### HACK!!!
RUN mkdir -p /src/main/resources/META-INF
ADD src/main/resources/META-INF/load.sql /src/main/resources/META-INF/
RUN mkdir /realm
ADD realm /opt/realm
ADD docker-entrypoint.sh /docker-entrypoint.sh

WORKDIR /

EXPOSE 8080
ENTRYPOINT [ "/docker-entrypoint.sh" ]
#ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y", "-jar", "qwanda-service-swarm.jar"]
