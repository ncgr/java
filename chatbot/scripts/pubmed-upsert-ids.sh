# usage: PubMedEmbeddingsUpserter
#  -a,--apikey <arg>   PubMed API key
#  -f,--file <arg>     file containing Abstract.toString() data
#  -i,--index <arg>    Pinecone index name
#  -l,--list <arg>     comma-separated list of PMIDs
#  -r,--retmax <arg>   value of retmax for abstract search
#  -t,--term <arg>     search term for abstract search

INDEX_NAME=$1
LIST=$2

java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.PubMedEmbeddingsUpserter --apikey=$PUBMED_API_KEY --index=$INDEX_NAME --list=$LIST
