#!/bin/sh
# usage: DataDumper
#  -f,--filter          filter the query on metadata given by --key and
#                       --value
#  -i,--index <arg>     Pinecone index name
#  -id,--id <arg>       id of a vector to fetch
#  -k,--key <arg>       key of the metadata for query filter or update
#  -t,--term <arg>      search term for query
#  -topk,--topk <arg>   Pinecone Top K value: maximum number of vectors to
#                       retrieve [5]
#  -v,--value <arg>     value of the metadata for query filter or update

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.DataDumper "$1" "$2" "$3" "$4" "$5" "$6"
