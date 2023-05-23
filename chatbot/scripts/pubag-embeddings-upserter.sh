# usage: PubAgEmbeddingsUpserter
#  -a,--apikey <arg>    PubAg API key
#  -f,--file <arg>      file containing Abstract.toString() data
#  -i,--index <arg>     Pinecone index name
#  -n,--perpage <arg>   per page number for search [20]
#  -p,--page <arg>      page number for search [1]
#  -t,--term <arg>      search term for abstract and title search

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.PubAgEmbeddingsUpserter -a $PUBAG_API_KEY "$1" "$2" "$3" "$4" "$5" "$6" "$7"
