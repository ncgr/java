#!/bin/sh

INDEX_NAME=$1
IDS=$2

curl -X GET \
    -H "Api-Key: $PINECONE_API_KEY" \
    "https://$INDEX_NAME-$PINECONE_PROJECT_NAME.svc.$PINECONE_ENVIRONMENT.pinecone.io/vectors/fetch?ids=$IDS"
