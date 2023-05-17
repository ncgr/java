#!/bin/sh
# usage: Pubmed
#  -a,--abstract       Search for abstracts
#  -d,--doi <arg>      value of DOI for esearch request
#  -k,--apikey <arg>   PubMed API key [optional]
#  -m,--retmax <arg>   maximum number of returned objects [20]
#  -p,--pmid <arg>     value of PMID for efetch or esummary request
#  -s,--summary        Search for summaries
#  -t,--text <arg>     value of text for esearch request

RETMAX=$1
TEXT=$2

## the javax.xml.accessExternalDTD property must be set to https!
java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-pubmed/lib/*" org.ncgr.pubmed.Pubmed --abstract --text="$TEXT" --apikey=$PUBMED_API_KEY --retmax=$RETMAX
