#!/bin/sh
# usage: Pubmed
#  -abs,--abstract       Retrieve PubMed abstracts
#  -art,--article        Retrieve PMC articles
#  -d,--doi <arg>        value of DOI for esearch request
#  -ids,--ids <arg>      comma-separated list of PMIDs or PMCIDs for
#                        efetch/esummary request
#  -key,--apikey <arg>   PubMed API key [optional]
#  -m,--retmax <arg>     maximum number of returned objects [20]
#  -sum,--summary        Retrieve PubMed summaries
#  -t,--text <arg>       value of text for esearch request

## the javax.xml.accessExternalDTD property must be set to https!
java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-pubmed/lib/*" org.ncgr.pubmed.Pubmed --abstract --ids="$1" --apikey=$PUBMED_API_KEY
