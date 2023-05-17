#!/bin/sh
# usage: Pubmed
#  -a,--abstract       Search for abstracts
#  -d,--doi <arg>      value of DOI for esearch request
#  -k,--apikey <arg>   PubMed API key [optional]
#  -m,--retmax <arg>   maximum number of returned objects [20]
#  -p,--pmid<arg>      comma-separated list of PMIDs for efetch or esummary request
#  -s,--summary        Search for summaries
#  -t,--text <arg>     value of text for esearch request

java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-pubmed/lib/*" org.ncgr.pubmed.Pubmed --summary --doi="$1" --apikey=$PUBMED_API_KEY
