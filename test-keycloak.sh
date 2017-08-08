#!/bin/bash
# Remember to install jq (e.g. brew install jq; yum install jq; apt-get install jq)

set -e
RED='\033[0;31m'
NORMAL='\033[0m'

ACCESS_TOKEN=$1
printf "$ACCESS_TOKEN \n\n"

USERS=`curl -k -v -H "Authorization: Bearer $ACCESS_TOKEN" http://127.0.0.1:8180/auth/realms/genny/users`
printf "$USERS \n\n"
