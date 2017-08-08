#!/bin/bash
# Remember to install jq (e.g. brew install jq; yum install jq; apt-get install jq)

set -e
RED='\033[0;31m'
NORMAL='\033[0m'

printf "${RED}Getting OAuth2 token from Keycloak (includes access_token, refresh_token, etc):${NORMAL}\n"
KEYCLOAK_RESPONSE=`curl -s -X POST https://bouncer.outcome-hub.com/auth/realms/genny/protocol/openid-connect/token  -H "Content-Type: application/x-www-form-urlencoded" -d 'username=service' -d 'password=Iamaserviceuser' -d 'grant_type=password' -d 'client_id=security-admin-console'  -d 'client_secret=b8383dbf-6ee5-45e2-8ad9-c1628968f1b0'`
printf "$KEYCLOAK_RESPONSE \n\n"

printf "${RED}Parsing access_token field, as we don't need the other elements:${NORMAL}\n"
ACCESS_TOKEN=`echo "$KEYCLOAK_RESPONSE" | jq -r '.access_token'`
printf "$ACCESS_TOKEN \n\n"

USERS=`curl -k -v -H "Authorization: Bearer $ACCESS_TOKEN" https://bouncer.outcome-hub.com/auth/admin/realms/genny/users`
printf "$USERS \n\n"
