#!/bin/sh
# usage: DataLoader
#  -f,--file <arg>    Name of file containing vector data and metadata to be
#                     loaded into Pinecone.
#  -i,--index <arg>   Pinecone index name

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.DataLoader $1 $2 $3 $4
