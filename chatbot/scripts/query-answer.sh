# usage: QueryAnswer
#  -f,--freqpenalty <arg>   OpenAi frequency penalty: larger reduces
#                           redundancy [0.0]
#  -i,--index <arg>         Pinecone index
#  -p,--prespenalty <arg>   OpenAi presence penalty: larger reduces
#                           redundancy [0.0]
#  -t,--temperature <arg>   OpenAi temperature [0.0,2.0]: larger means more
#                           random completion [0.0]
#  -topk,--topk <arg>       Pinecone Top K: maximum number of contexts to
#                           retrieve [5]

java -cp "build/install/ncgr-chatbot/lib/*" org.ncgr.chatbot.QueryAnswer $1 $2 $3 $4 $5
