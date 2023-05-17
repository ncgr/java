# usage: PubMedEmbeddingsUpserter
#  -a,--apikey <arg>   PubMed API key
#  -f,--file <arg>     file containing Abstract.toString() data
#  -i,--index <arg>    Pinecone index name
#  -l,--list <arg>     comma-separated list of PMIDs
#  -r,--retmax <arg>   value of retmax for abstract search
#  -t,--term <arg>     search term for abstract search

INDEX_NAME=$1
FILE=$2

java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.PubMedEmbeddingsUpserter -a $PUBMED_API_KEY -i $INDEX_NAME -f $FILE
