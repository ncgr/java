#!/bin/sh
# usage: Abstract
#  -a,--apikey <arg>   PubMed API key [optional]
#  -d,--doi <arg>      value of DOI for esearch request
#  -m,--retmax <arg>   maximum number of returned Summaries [20]
#  -p,--pmids <arg>    comma-separated list of PMIDs for esummary request
#  -t,--text <arg>     value of text for esearch request

## the javax.xml.accessExternalDTD property must be set to https!
java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-pubmed/lib/*" org.ncgr.pubmed.Pubmed --doi="$1" --apikey=$PUBMED_API_KEY
