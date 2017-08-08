#!/bin/bash

if [ -z "${1}" ]; then
   version="latest"
else
   version="${1}"
fi

docker push gennyproject/qwanda-service:"${version}"
docker tag -f gennyproject/qwanda-service:"${version}"  gennyproject/qwanda-service:latest
docker push gennyproject/qwanda-service:latest

