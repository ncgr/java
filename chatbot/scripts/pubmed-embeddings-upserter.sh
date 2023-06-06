# usage: PubMedEmbeddingsUpserter
#  -a,--apikey <arg>   PubMed API key
#  -d,--doi <arg>      DOI for abstract search
#  -f,--file <arg>     file containing Abstract.toString() data
#  -i,--index <arg>    Pinecone index name
#  -l,--list <arg>     comma-separated list of PMIDs
#  -r,--retmax <arg>   value of retmax for abstract search
#  -t,--term <arg>     search term for abstract search
#  -u,--update         update mode: only upsert new PMIDs

java -Djavax.xml.accessExternalDTD=https -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.PubMedEmbeddingsUpserter -a $PUBMED_API_KEY "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"
