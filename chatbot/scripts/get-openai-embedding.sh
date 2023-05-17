QUERY=$1

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.openai.OpenAi "$QUERY"
