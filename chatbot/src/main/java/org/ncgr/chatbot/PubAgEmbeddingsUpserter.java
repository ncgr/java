package org.ncgr.chatbot;

import org.ncgr.pubag.Abstract;
import org.ncgr.pubag.Pubag;

import org.ncgr.chatbot.pinecone.Pinecone;

import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;

import io.pinecone.proto.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.xmlbeans.XmlException;

import org.xml.sax.SAXException;

/**
 * Provides methods to query PubAg for abstracts, generate embeddings from them with OpenAI, and upsert them to a Pinecone index.
 * Also can load abstracts from text files dumped from Abstract.toString().
 */
public class PubAgEmbeddingsUpserter {

    // the OpenAI embedding model to use
    static String EMBED_MODEL = "text-embedding-ada-002";

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, JAXBException, XMLStreamException, XmlException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option indexOption = new Option("i", "index", true, "Pinecone index name");
        indexOption.setRequired(true);
        options.addOption(indexOption);

        Option apikeyOption = new Option("a", "apikey", true, "PubAg API key");
        apikeyOption.setRequired(true);
        options.addOption(apikeyOption);
        
        Option termOption = new Option("t", "term", true, "search term for abstract and title search");
        termOption.setRequired(false);
        options.addOption(termOption);
        
        Option pageOption = new Option("p", "page", true, "page number for search [1]");
        pageOption.setRequired(false);
        options.addOption(pageOption);

        Option perpageOption = new Option("n", "perpage", true, "per page number for search [20]");
        perpageOption.setRequired(false);
        options.addOption(perpageOption);

        Option fileOption = new Option("f", "file", true, "file containing Abstract.toString() data");
        fileOption.setRequired(false);
        options.addOption(fileOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("PubAgEmbeddingsUpserter", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("PubAgEmbeddingsUpserter", options);
            System.exit(1);
            return;
        }

        // required parameters
        String pineconeIndexName = cmd.getOptionValue("index");
        String apikey = cmd.getOptionValue("apikey");
        
        // optional parameters
        int page = 1;
        if (cmd.hasOption("page")) page = Integer.parseInt(cmd.getOptionValue("page"));
        int perpage = 20;
        if (cmd.hasOption("perpage")) perpage = Integer.parseInt(cmd.getOptionValue("perpage"));
        
        // other stuff from environment variables
        String openaiApiKey = System.getenv().get("OPENAI_API_KEY");
        String pineconeProjectName = System.getenv().get("PINECONE_PROJECT_NAME");
        String pineconeApiKey = System.getenv().get("PINECONE_API_KEY");
        String pineconeEnvironment = System.getenv().get("PINECONE_ENVIRONMENT");

        OpenAiService openaiService = new OpenAiService(openaiApiKey);
        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);

        if (cmd.hasOption("file")) {
            String filename = cmd.getOptionValue("file");
            List<Abstract> abstracts = withoutPmid(Abstract.load(filename));
            if (abstracts.size()>0) {
                upsertVectors(openaiService, pinecone, abstracts);
            } else {
                System.out.println("No abstracts to upsert.");
            }
        }

        if (cmd.hasOption("term")) {
            String term = cmd.getOptionValue("term");
            List<Abstract> abstracts = withoutPmid(Pubag.searchAbstractOrTitleText(term, page, perpage, apikey));
            if (abstracts.size()>0) {
                upsertVectors(openaiService, pinecone, abstracts);
            } else {
                System.out.println("No abstracts to upsert.");
            }
            return;
        }
    }
    
    /**
     * Return abstracts that lack PMID.
     */
    static List<Abstract> withoutPmid(List<Abstract> abstracts) {
        List<Abstract> without = new ArrayList<>();
        for (Abstract a : abstracts) {
            if (a.getPMID() == null) without.add(a);
        }
        return without;
    }

    /**
     * Get String contexts from Abstract text for embedding.
     */
    static List<String> getContexts(List<Abstract> abstracts) {
        List<String> contexts = new ArrayList<>();
        for (Abstract a : abstracts) {
            contexts.add(a.getText());
        }
        return contexts;
    }

    /**
     * Get embeddings from OpenAI for a List of contexts.
     */
    static List<Embedding> getEmbeddings(OpenAiService service, List<String> contexts) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
            .model(EMBED_MODEL)
            .input(contexts)
            .build();
        return service.createEmbeddings(embeddingRequest).getData();
    }

    /**
     * Get embedding vectors for Pinecone with metadata from Abstract.
     */
    static List<Vector> getVectors(List<Embedding> embeddings, List<Abstract> abstracts) {
        List<Vector> vectors = new ArrayList<>();
        for (Embedding embedding : embeddings) {
            int index = embedding.getIndex();
            Abstract a = abstracts.get(index);
            // prepend ID in case it overlaps with a PMID
            String id = "PubAg:" + a.getId();
            // form metadata; title and abstract should never be null
            Struct.Builder metadataBuilder = Struct
                .newBuilder()
                .putFields("title", Value.newBuilder().setStringValue(a.getTitle()).build())
                .putFields("abstract", Value.newBuilder().setStringValue(a.getText()).build());
            // add non-null metadata
            if (a.getDOI() != null) {
                metadataBuilder.putFields("DOI", Value.newBuilder().setStringValue(a.getDOI()).build());
            }
            if (a.getKeywords().size() > 0) {
                metadataBuilder.putFields("keywords", Value.newBuilder().setStringValue(a.getKeywords().toString()).build());
            }
            Struct metadata = metadataBuilder.build();
            // annoyance: Pinecone Vector wants Float embeddings, OpenAI provides Double embeddings!
            List<Float> floatEmbedding = new ArrayList<>();
            for (Double d : embedding.getEmbedding()) {
                floatEmbedding.add(d.floatValue());
            }
            vectors.add(Vector.newBuilder()
                        .setId(id)
                        .setMetadata(metadata)
                        .addAllValues(floatEmbedding)
                        .build());
        }
        return vectors;
    }
        
    /**
     * Get embeddings from OpenAI, form Vectors, and upsert them to Pinecone.
     * Metadata is added to the Vectors from the abstracts.
     * We limit to 100 abstracts per call to stay under limits.
     */
    static void upsertVectors(OpenAiService openaiService, Pinecone pinecone, List<Abstract> abstracts) {
        int start = 0;
        int end = Math.min(100, abstracts.size());
        boolean hasMore = true;
        while (hasMore) {
            List<Abstract> subList = abstracts.subList(start, end);
            // get the contexts
            List<String> contexts = getContexts(subList);
            // get the embeddings for these contexts
            List<Embedding> embeddings = getEmbeddings(openaiService, contexts);
            // form Vectors with metadata from these contexts and embeddings
            List<Vector> vectors = getVectors(embeddings, subList);
            // upsert the vectors to Pinecone
            pinecone.upsertVectors(vectors);
            System.out.println("Upserted "+vectors.size()+" embedding vectors into Pinecone index.");
            // increment sub-list indexes
            start = Math.min(start + 100, abstracts.size());
            end = Math.min(end + 100, abstracts.size());
            hasMore = start < abstracts.size();
        }
    }
}
