INDEX_NAME = $1
TEXT_FILE = $2
java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.TextEmbeddingsUpserter $INDEX_NAME $TEXT_FILE 
