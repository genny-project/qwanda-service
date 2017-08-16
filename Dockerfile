FROM openjdk:8u131-jre-alpine 

#RUN set -x \
#    && apt-get update --quiet \
#    && apt-get install --quiet --yes --no-install-recommends jq sed  iputils-ping vim  \
#    && apt-get clean

RUN apk update \
 && apk add jq \
 && apk add sed \
 && apk add vim \
 && apk add bash \
 && rm -rf /var/cache/apk/*

ADD target/qwanda-service-swarm.jar /opt/qwanda-service-swarm.jar

#### HACK!!!
RUN mkdir -p /opt/src/main/resources/META-INF
ADD src/main/resources/META-INF/load.sql /opt/src/main/resources/META-INF/
RUN mkdir /realm
ADD realm /opt/realm
ADD docker-entrypoint2.sh /opt/docker-entrypoint2.sh

WORKDIR /opt

EXPOSE 8080
ENTRYPOINT [ "/opt/docker-entrypoint2.sh" ]
#ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y", "-jar", "qwanda-service-swarm.jar"]
