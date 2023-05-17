#!/bin/sh
# usage: Cleaner
#  -i,--index <arg>     Pinecone index
#  -ids,--ids <arg>     comma-separated list of vector ids
#  -k,--key <arg>       metadata key to be removed
#  -topk,--topk <arg>   maximum number of vectors to retrieve in a query [5]
#  -v,--value <arg>     value of the metadata item given by --key to
#                       restrict which are removed

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.Cleaner $1 $2 $3 $4 $5
