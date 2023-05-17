package org.ncgr.chatbot.openai;

import java.util.Collections;
import java.util.List;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;

import com.theokanning.openai.service.OpenAiService;

/**
 * Class to retrieve embeddings from OpenAI.
 */
public class EmbeddingTest {

    // the OpenAI embedding model to use
    static String EMBED_MODEL = "text-embedding-ada-002";

    public static void main(String[] args) {
        String token = System.getenv("OPENAI_API_KEY");
        OpenAiService service = new OpenAiService(token);

        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
            .model("text-embedding-ada-002")
            .input(Collections.singletonList("List photosynthesis genes."))
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
