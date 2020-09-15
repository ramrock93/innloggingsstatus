#!/bin/env sh
echo "- exporting SERVICEUSER_USERNAME"
export SERVICEUSER_USERNAME=$(cat /secret/serviceuser/username)

echo "- exporting SERVICEUSER_PASSWORD"
export SERVICEUSER_PASSWORD=$(cat /secret/serviceuser/password)

echo "- exporting STS_API_GW_KEY"
export STS_API_GW_KEY=$(cat /secret/sts_apiKey/x-nav-apiKey)

echo "- exporting PDL_API_GW_KEY"
export PDL_API_GW_KEY=$(cat /secret/pdl_apiKey/x-nav-apiKey)

