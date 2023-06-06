# usage: PubAgEmbeddingsUpserter
#  -a,--apikey <arg>    PubAg API key
#  -f,--file <arg>      file containing Abstract.toString() data
#  -i,--index <arg>     Pinecone index name
#  -t,--term <arg>      search term for abstract and title search
#  -u,--update          update mode: only upsert new abstracts

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.PubAgEmbeddingsUpserter -a $PUBAG_API_KEY "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"
