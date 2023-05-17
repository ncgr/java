#!/bin/sh
INDEX_NAME=$1
curl -X GET https://$INDEX_NAME-$PINECONE_PROJECT_NAME.svc.$PINECONE_ENVIRONMENT.pinecone.io/describe_index_stats \
    -H "Api-Key: $PINECONE_API_KEY"
echo ""
