package org.ncgr.chatbot;

import org.ncgr.pubmed.Abstract;
import org.ncgr.pubmed.Pubmed;

import org.ncgr.ai.pinecone.Pinecone;

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

import com.google.protobuf.Value;
import com.google.protobuf.Struct;

import com.theokanning.openai.OpenAiHttpException;
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
 * Provides methods to query PubMed for abstracts, generate embeddings from them with OpenAI, and upsert them to a Pinecone index.
 * Alternatively, a comma-separated list of PMIDs may be supplied.
 * Alternatively, a file containing org.ncgr.pubmed.Abstract.toString() data may be supplied.
 */
public class PubMedEmbeddingsUpserter {

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

        Option apikeyOption = new Option("a", "apikey", true, "PubMed API key");
        apikeyOption.setRequired(true);
        options.addOption(apikeyOption);
        
        Option retmaxOption = new Option("r", "retmax", true, "value of retmax for abstract search");
        retmaxOption.setRequired(false);
        options.addOption(retmaxOption);

        Option termOption = new Option("t", "term", true, "search term for abstract search");
        termOption.setRequired(false);
        options.addOption(termOption);

        Option listOption = new Option("l", "list", true, "comma-separated list of PMIDs");
        listOption.setRequired(false);
        options.addOption(listOption);

        Option fileOption = new Option("f", "file", true, "file containing Abstract.toString() data");
        fileOption.setRequired(false);
        options.addOption(fileOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("PubMedEmbeddingsUpserter", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("PubMedEmbeddingsUpserter", options);
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

        // our services
        OpenAiService openaiService = new OpenAiService(openaiApiKey);
        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);

        if (cmd.hasOption("retmax") && cmd.hasOption("term")) {
            int retmax = Integer.parseInt(cmd.getOptionValue("retmax"));
            String term = cmd.getOptionValue("term");
            List<Abstract> abstracts = Pubmed.searchAbstractTitleAndText(term, retmax, apikey);
            upsertVectors(openaiService, pinecone, abstracts);
        } else if (cmd.hasOption("list")) {
            List<String> idList = Arrays.asList(cmd.getOptionValue("list").split(","));
            List<Abstract> abstracts = Pubmed.getAbstracts(idList, apikey);
            upsertVectors(openaiService, pinecone, abstracts);
        } else if (cmd.hasOption("file")) {
            String filename = cmd.getOptionValue("file");
            List<Abstract> abstracts = Abstract.load(filename);
            upsertVectors(openaiService, pinecone, abstracts);
        } else {
            System.err.println("You must supply either --retmax and --term for a search, or --list with a comma-separated list of PMIDs.");
            System.exit(1);
        }
    }

    /**
     * Get String contexts from just abstract text for embedding.
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
        try {
            return service.createEmbeddings(embeddingRequest).getData();
        } catch (OpenAiHttpException ex) {
            System.err.println(ex.getMessage());
            System.err.println(contexts);
            return new ArrayList<Embedding>();
        }
    }

    /**
     * Get embedding vectors for Pinecone from embeddings, adding metadata from abstract.
     */
    static List<Vector> getVectors(List<Embedding> embeddings, List<Abstract> abstracts) {
        List<Vector> vectors = new ArrayList<>();
        for (Embedding embedding : embeddings) {
            int index = embedding.getIndex();
            Abstract a = abstracts.get(index);
            
            // form metadata; title, abstract and PMID should definitely not be null!
            Struct.Builder metadataBuilder = Struct
                .newBuilder()
                .putFields("title", Value.newBuilder().setStringValue(a.getTitle()).build())
                .putFields("abstract", Value.newBuilder().setStringValue(a.getText()).build())
                .putFields("PMID", Value.newBuilder().setStringValue(a.getPMID()).build());
            // add metadata for things that may be missing
            if (a.getKeywords().size() > 0) {
                metadataBuilder.putFields("keywords", Value.newBuilder().setStringValue(a.getKeywords().toString()).build());
            }
            if (a.getPMCID() != null) {
                metadataBuilder.putFields("PMCID", Value.newBuilder().setStringValue(a.getPMCID()).build());
            }
            if (a.getDOI() != null) {
                metadataBuilder.putFields("DOI", Value.newBuilder().setStringValue(a.getDOI()).build());
            }
            Struct metadata = metadataBuilder.build();
            // annoyance: Pinecone Vector wants Float embeddings, OpenAI provides Double embeddings!
            List<Float> floatEmbedding = new ArrayList<>();
            for (Double d : embedding.getEmbedding()) {
                floatEmbedding.add(d.floatValue());
            }
            // Add this Vector using PMID as ID
            vectors.add(Vector.newBuilder()
                        .setId(a.getPMID())
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
        // get the contexts
        List<String> contexts = getContexts(abstracts);
        // get the embeddings for these contexts
        List<Embedding> embeddings = getEmbeddings(openaiService, contexts);
        if (embeddings.size()>0) {
            // form Vectors with embeddings plus metadata from the abstracts
            List<Vector> vectors = getVectors(embeddings, abstracts);
            // upsert the vectors to Pinecone
            pinecone.upsertVectors(vectors);
            System.out.println("Upserted "+vectors.size()+" embedding vectors into Pinecone index.");
        }
    }
}
