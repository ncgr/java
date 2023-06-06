package org.ncgr.chatbot;

import org.ncgr.pubag.Abstract;
import org.ncgr.pubag.Pubag;

import org.ncgr.chatbot.openai.OpenAi;
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

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;

import io.pinecone.proto.ScoredVector;
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
 * Note: if a DOI or title is already present in the index, the article will NOT be uploaded.
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
        
        Option fileOption = new Option("f", "file", true, "file containing Abstract.toString() data");
        fileOption.setRequired(false);
        options.addOption(fileOption);

        Option updateOption = new Option("u", "update", false, "update mode: only upsert new abstracts");
        updateOption.setRequired(false);
        options.addOption(updateOption);

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
        
        // other stuff from environment variables
        String openaiApiKey = System.getenv().get("OPENAI_API_KEY");
        String pineconeProjectName = System.getenv().get("PINECONE_PROJECT_NAME");
        String pineconeApiKey = System.getenv().get("PINECONE_API_KEY");
        String pineconeEnvironment = System.getenv().get("PINECONE_ENVIRONMENT");

        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);

	// retrieve abstracts
	List<Abstract> abstracts = new ArrayList<>();
        if (cmd.hasOption("file")) {
            String filename = cmd.getOptionValue("file");
            abstracts = Abstract.load(filename);
        } else if (cmd.hasOption("term")) {
            String term = cmd.getOptionValue("term");
            // cycle through pages until no more found
            boolean haveMore = true;
            int page = 1;
            System.out.print("## Page ");
            while (haveMore) {
                List<Abstract> pageAbstracts = Pubag.searchAbstractOrTitleText(term, page, 100, apikey);
                if (pageAbstracts.size() > 0) {
                    System.out.print(page + ":" + pageAbstracts.size() + " ");
                    abstracts.addAll(pageAbstracts);
                } else {
                    System.out.println("done.");
                    haveMore = false;
                }
                page++;
            }
        }
        System.out.println("## Found " + abstracts.size() + " total abstracts.");

        if (cmd.hasOption("update") && abstracts.size() > 0) {
            Map<String,Abstract> abstractMap = new HashMap<>(); // keyed by Pinecone ID
            List<String> ids = new ArrayList<>();
            for (Abstract a : abstracts) {
                String id = formPineconeId(a.getId());
                abstractMap.put(id, a);
                ids.add(id);
            }
            Map<String,Vector> existingVectors = pinecone.fetchVectors(ids);
            // remove existing abstracts from map
            for (String id : existingVectors.keySet()) {
                if (abstractMap.containsKey(id)) abstractMap.remove(id);
            }
            // load abstracts with remaining ones in map
            abstracts = new ArrayList<>();
            for (Abstract a : abstractMap.values()) {
                abstracts.add(a);
            }
            System.out.println("## Found " + abstracts.size() + " new abstracts.");
        }

        // remove abstracts that lack text
        List<Abstract> all = new ArrayList<>(abstracts); // avoid concurrent mod
        for (Abstract a : all) {
            if ((a.getText() == null) || (a.getText().length() == 0)) {
                abstracts.remove(a);
            }
        }

	// upsert our abstracts
	if (abstracts!=null && abstracts.size()>0) {
	    OpenAi openai = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);
	    upsertVectors(openai, pinecone, abstracts);
	} else {
	    System.out.println("## No abstracts were upserted.");
	}
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
     * Get embedding vectors for Pinecone with metadata from Abstract.
     */
    static List<Vector> getVectors(List<Embedding> embeddings, List<Abstract> abstracts) {
        List<Vector> vectors = new ArrayList<>();
        for (Embedding embedding : embeddings) {
            int index = embedding.getIndex();
            Abstract a = abstracts.get(index);
            // form metadata; title and abstract should never be null
            Struct.Builder metadataBuilder = Struct
                .newBuilder()
                .putFields("title", Value.newBuilder().setStringValue(a.getTitle()).build())
                .putFields("abstract", Value.newBuilder().setStringValue(a.getText()).build());
            // add non-null metadata
            if (a.getDOI() != null) {
                metadataBuilder.putFields("DOI", Value.newBuilder().setStringValue(a.getDOI()).build());
            }
	    if (a.getPMID() != null) {
                metadataBuilder.putFields("PMID", Value.newBuilder().setStringValue(a.getPMID()).build());
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
            // add the vector with an ID of the form PubAg-{id}
            vectors.add(Vector.newBuilder()
                        .setId(formPineconeId(a.getId()))
                        .setMetadata(metadata)
                        .addAllValues(floatEmbedding)
                        .build());
        }
        return vectors;
    }

    /**
     * Form the Pinecone id for a PubAg article
     */
    static String formPineconeId(String id) {
        return "PubAg-" + id;
    }

    /**
     * Get embeddings from OpenAI, form Vectors, and upsert them to Pinecone.
     * Metadata is added to the Vectors from the abstracts.
     */
    static void upsertVectors(OpenAi openai, Pinecone pinecone, List<Abstract> abstracts) {
        List<String> contexts = getContexts(abstracts);
        if (contexts.size() > 0) {
            try {
                List<Embedding> embeddings = openai.getEmbeddings(contexts);
                // form Vectors with embeddings plus metadata from the abstracts
                List<Vector> vectors = getVectors(embeddings, abstracts);
                // upsert the vectors to Pinecone
                pinecone.upsertVectors(vectors);
                System.out.println("Upserted "+vectors.size()+" embedding vectors into Pinecone index.");
            } catch (OpenAiHttpException ex) {
                System.err.println(ex);
                System.exit(1);
            }
        }
    }
    
}
