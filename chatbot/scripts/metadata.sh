#!/bin/sh
# usage: Metadata
#  -f,--filter              filter a query on metadata given by --key and
#                           --value
#  -i,--index <arg>         Pinecone index
#  -id,--id <arg>           id of a vector
#  -k,--key <arg>           key of the metadata for query filter or update
#  -mk,--missingkey <arg>   return vectors that are missing this metadata
#                           key
#  -q,--query               perform a query
#  -t,--term <arg>          search term for query
#  -topk,--topk <arg>       Pinecone Top K value: maximum number of contexts
#                           to retrieve [5]
#  -u,--update              update the metadata given by --key and --value
#                           for the vector given by --id
#  -v,--value <arg>         value of the metadata for query filter or update

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.Metadata $1 $2 $3 $4 $5 $6 $7 $8 $9
