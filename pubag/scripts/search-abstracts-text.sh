#!/bin/sh
# usage: Pubag
#  -key,--apikey <arg>   PubAg API key
#  -n,--perpage <arg>    number of records per page [20]
#  -p,--page <arg>       page number to retrieve [1]
#  -t,--term <arg>       value of search term for PubAg search

PERPAGE=$1
PAGE=$2
TERM=$3

java -cp "build/install/ncgr-pubag/lib/*" org.ncgr.pubag.Pubag --apikey=$PUBAG_API_KEY --perpage=$PERPAGE --page=$PAGE --term="$TERM"
