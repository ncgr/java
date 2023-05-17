#!/bin/sh

INDEX_NAME=$1
ID=$2
KEY=$3
VALUE=$4

## need double-quote to support variables!
curl "https://$INDEX_NAME-$PINECONE_PROJECT_NAME.svc.$PINECONE_ENVIRONMENT.pinecone.io/vectors/update" \
     -H "Content-Type: application/json" \
     -H "Api-Key: $PINECONE_API_KEY" \
     -d "{ \"id\": \"$ID\", \"setMetadata\": { \"$KEY\": \"$VALUE\" } }"
