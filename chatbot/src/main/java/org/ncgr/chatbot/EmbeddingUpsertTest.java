package org.ncgr.chatbot;

import java.util.Collections;
import java.util.List;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;

/**
 * Class to test retrieval of embeddings from OpenAI and upserting them to Pinecone.
 */
public class EmbeddingUpsertTest {

    // the OpenAI embedding model to use
    static String EMBED_MODEL = "text-embedding-ada-002";

    public static void main(String[] args) {
        if (args.length<2) {
            System.err.println("Usage: EbeddingUpsertTest <index> <text>");
            System.exit(1);
        }
        String index = args[0];
        String text = args[1];
        
        String openaiApiKey = System.getenv().get("OPENAI_API_KEY");
        String pineconeProjectName = System.getenv().get("PINECONE_PROJECT_NAME");
        String pineconeApiKey = System.getenv().get("PINECONE_API_KEY");
        String pineconeEnvironment = System.getenv().get("PINECONE_ENVIRONMENT");
        String pineconeIndexName = System.getenv().get("PINECONE_INDEX_NAME");
        
        OpenAiService service = new OpenAiService(openaiApiKey);

        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
            .model(EMBED_MODEL)
            .input(Collections.singletonList(text))
            .build();

        List<Embedding> embeddings = service.createEmbeddings(embeddingRequest).getData();

        for (Embedding embedding : embeddings) {
            List<Double> vector = embedding.getEmbedding();
            System.out.println("object: " + embedding.getObject());
            System.out.println("index: " + embedding.getIndex());
            System.out.println("vector: " + vector);
        }
    }
}
