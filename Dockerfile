FROM openjdk:9-b181-jre 
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

#### HACK!!!
RUN mkdir -p /src/main/resources/META-INF
ADD src/main/resources/META-INF/load.sql /src/main/resources/META-INF/
RUN mkdir /realm
ADD realm /opt/realm
ADD docker-entrypoint.sh /docker-entrypoint.sh

WORKDIR /

EXPOSE 8080
ENTRYPOINT [ "/docker-entrypoint.sh" ]
