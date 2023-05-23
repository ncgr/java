INDEX=$1
TEXTFILE=$2
java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.TextEmbeddingsUpserter $INDEX $TEXTFILE 
