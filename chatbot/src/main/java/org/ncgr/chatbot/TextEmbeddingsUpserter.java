package org.ncgr.chatbot;

import org.ncgr.chatbot.pinecone.Pinecone;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;

import io.pinecone.proto.Vector;

/**
 * Provides methods to upsert abstracts with title and DOI from a text file to a Pincone index, generating the embeddings with OpenAI.
 * The format of the text file is:
 *
 * TITLE: Evidence for two gene pools of the Lima bean,Phaseolus lunatus L., in the Americas
 * ABSTRACT:
 * The lima bean, Phaseolus lunatus L., is a bean species with a broad distribution in the Americas that rivals...
 * ID: BF02310680
 * DOI: 10.1002/star.200500398
 *
 * Entries are separated by a blank line. D
 */
public class TextEmbeddingsUpserter {

    // the OpenAI embedding model to use
    static String EMBED_MODEL = "text-embedding-ada-002";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length<2) {
            System.err.println("Usage: TextEmbeddingsUpserter <pinecone-index> <text-file>");
            System.exit(1);
        }
        String pineconeIndexName = args[0];
        String filename = args[1];
        
        String openaiApiKey = System.getenv().get("OPENAI_API_KEY");

        String pineconeProjectName = System.getenv().get("PINECONE_PROJECT_NAME");
        String pineconeApiKey = System.getenv().get("PINECONE_API_KEY");
        String pineconeEnvironment = System.getenv().get("PINECONE_ENVIRONMENT");

        OpenAiService openaiService = new OpenAiService(openaiApiKey);
        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
        
        List<TextAbstract> abstracts = new ArrayList<>();
        String title = null;
        String text = null;
        String id = null;
        String doi = null;
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("TITLE:")) {
                title = line.split("TITLE: ")[1];
            } else if (line.startsWith("ABSTRACT:")) {
                text = "";
            } else if (line.startsWith("ID:")) {
                id = line.split("ID: ")[1];
            } else if (line.startsWith("DOI:")) {
                doi = line.split("DOI: ")[1];
            } else if (line.trim().length()==0) {
                // store entry
                TextAbstract a = new TextEmbeddingsUpserter.TextAbstract();
                a.title = title;
                a.text = text;
                a.id = id;
                if (doi!=null) a.doi = doi;
                abstracts.add(a);
                title = null;
                text = null;
                id = null;
                doi = null;
            } else {
                // append line to text
                text += " " + line;
            }
        }

        // for (TextAbstract a : abstracts) {
        //     System.out.println("- TITLE");
        //     System.out.println(a.title);
        //     System.out.println("--- ABSTRACT");
        //     System.out.println(a.text);
        //     System.out.println("--- ID");
        //     System.out.println(a.id);
        //     System.out.println("--- DOI");
        //     System.out.println(a.doi);
        // }
        
        upsertVectors(openaiService, pinecone, abstracts);
    }

    /**
     * Get embeddings from OpenAI, form Vectors, and upsert them to Pinecone.
     * Metadata is added to the Vectors from the abstracts.
     */
    static void upsertVectors(OpenAiService openaiService, Pinecone pinecone, List<TextAbstract> abstracts) {
        List<Vector> vectors = new ArrayList<>();
        // get the contexts
        List<String> contexts = new ArrayList<>();
        for (TextAbstract a : abstracts) {
            String context = "Abstract:\n" + a.text;
            // append title, PMID and DOI to abstract text
            context += "\nTitle: " + a.title;
            if (a.doi!=null) context += "\nDOI: " + a.doi;
            contexts.add(context);
        }
        // get the embeddings for these contexts
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
            .model(EMBED_MODEL)
            .input(contexts)
            .build();
        // OpenAI embedding call
        List<Embedding> embeddings = openaiService.createEmbeddings(embeddingRequest).getData();
        // form Vectors with metadata
        for (Embedding embedding : embeddings) {
            int index = embedding.getIndex();
            TextAbstract a = abstracts.get(index);
            Struct.Builder metadataBuilder = Struct
                .newBuilder()
                .putFields("title", Value.newBuilder().setStringValue(a.title).build())
                .putFields("text", Value.newBuilder().setStringValue(a.text).build());
            if (a.doi != null) {
                metadataBuilder.putFields("DOI", Value.newBuilder().setStringValue(a.doi).build());
            }
            Struct metadata = metadataBuilder.build();
            // annoyance: Pinecone Vector wants Float embeddings, OpenAI provides Double embeddings!
            List<Float> floatEmbedding = new ArrayList<>();
            for (Double d : embedding.getEmbedding()) {
                floatEmbedding.add(d.floatValue());
            }
            vectors.add(Vector.newBuilder()
                        .setId(a.id)
                        .setMetadata(metadata)
                        .addAllValues(floatEmbedding)
                        .build());
        }
        // upsert the vectors to Pinecone
        pinecone.upsertVectors(vectors);
        System.out.println("Upserted "+vectors.size()+" embedding vectors into Pinecone index.");
    }

    /**
     * Encapsulate an abstract gleaned from a text file.
     */
    static class TextAbstract {
        String title;
        String text;
        String id;
        String doi;
    }
}
