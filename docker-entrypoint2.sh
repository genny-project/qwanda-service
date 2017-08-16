#!/bin/bash
KEYCLOAK_JSON_DIR=/realm
KEYCLOAK_ORIGINAL_JSON_DIR=/opt/realm

# copy all the keycloak files so they may be modified
cp -rf ${KEYCLOAK_ORIGINAL_JSON_DIR}/* ${KEYCLOAK_JSON_DIR}/

# change the package.json file
function escape_slashes {
    /bin/sed 's/\//\\\//g'
}

function change_line {
  eval OLD_LINE_PATTERN="$1"
  eval NEW_LINE="$2"
  eval FILE="$3"

    local NEW=$(echo "${NEW_LINE}" | escape_slashes)
    /bin/sed -i  '/'"${OLD_LINE_PATTERN}"'/s/.*/'"${NEW}"'/' "${FILE}"
}


for i in `ls ${KEYCLOAK_JSON_DIR}` ; do

   OLD_LINE_KEY="auth-server-url"
   NEW_LINE="\"auth-server-url\": \"${KEYCLOAKURL}/auth\","
   change_line "\${OLD_LINE_KEY}" "\${NEW_LINE}" "\${KEYCLOAK_JSON_FILE}"
done

#Set some ENV by extracting from keycloak.json file
export KEYCLOAK_REALM=`jq '.realm' /opt/realm/keycloak.json`
export KEYCLOAK_URL=`jq '.["auth-server-url"]' /opt/realm/keycloak.json`
export KEYCLOAK_CLIENTID=`jq '.resource' /opt/realm/keycloak.json`
export KEYCLOAK_SECRET=`jq '.secret' /opt/realm/keycloak.json`

echo "KEYCLOAK REALM= ${KEYCLOAK_REALM}"
echo "KEYCLOAK URL= ${KEYCLOAK_URL}"
echo "KEYCLOAK CLIENTID= ${KEYCLOAK_CLIENTID}"

java -jar ${JAVA_OPT_KEYCLOAK_PATH}   -Djava.net.preferIPv6Addresses=false   -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true   qwanda-service-swarm.jar


