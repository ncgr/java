package org.ncgr.chatbot;

import org.ncgr.pubmed.Abstract;
import org.ncgr.pubmed.Pubmed;

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

import com.google.protobuf.Value;
import com.google.protobuf.Struct;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;

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

	Option doiOption = new Option("d", "doi", true, "DOI for abstract search");
	doiOption.setRequired(false);
	options.addOption(doiOption);

        Option listOption = new Option("l", "list", true, "comma-separated list of PMIDs");
        listOption.setRequired(false);
        options.addOption(listOption);

        Option fileOption = new Option("f", "file", true, "file containing Abstract.toString() data");
        fileOption.setRequired(false);
        options.addOption(fileOption);

        Option updateOption = new Option("u", "update", false, "update mode: only upsert new PMIDs");
        updateOption.setRequired(false);
        options.addOption(updateOption);
        
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

        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
        
	// our abstracts to upsert
	List<Abstract> abstracts = new ArrayList<>();
	
        if (cmd.hasOption("retmax") && cmd.hasOption("term")) {
            int retmax = Integer.parseInt(cmd.getOptionValue("retmax"));
            String term = cmd.getOptionValue("term");
            if (cmd.hasOption("update")) {
                // only get new abstracts, for which Pinecone returns no vector
                List<String> pmids = Pubmed.searchPMIDTitleAndText(term, retmax, apikey);
                System.out.println("## " + pmids.size() + " total PMIDs were found.");
                if (pmids.size() > 0) {
                    Map<String,String> idMap = new HashMap<>(); // Pinecone ids keyed by PMID
                    for (String pmid : pmids) idMap.put(pmid, formPineconeId(pmid));
                    Map<String,Vector> existingVectors = pinecone.fetchVectors(new ArrayList(idMap.values()));
                    List<String> pmidsToUpsert = new ArrayList<>();
                    for (String pmid : idMap.keySet()) {
                        String id = idMap.get(pmid);
                        if (!existingVectors.containsKey(id)) pmidsToUpsert.add(pmid);
                    }
                    if (pmidsToUpsert.size() > 0) {
                        System.out.println("## " + pmidsToUpsert.size() + " are new PMIDs.");
                        abstracts = Pubmed.getAbstracts(pmidsToUpsert, apikey);
                        System.out.println("## " + abstracts.size() + " new abstracts were fetched.");
                    }
                }
            } else {
                // get all abstracts from PubMed search
                abstracts = Pubmed.searchAbstractTitleAndText(term, retmax, apikey);
                System.out.println("## " + abstracts.size() + " total abstracts were found.");
            }
        } else if (cmd.hasOption("list")) {
            List<String> idList = Arrays.asList(cmd.getOptionValue("list").split(","));
            abstracts = Pubmed.getAbstracts(idList, apikey);
        } else if (cmd.hasOption("file")) {
            String filename = cmd.getOptionValue("file");
            abstracts = Abstract.load(filename);
	} else if (cmd.hasOption("doi")) {
	    String doi = cmd.getOptionValue("doi");
	    Abstract a = Pubmed.searchAbstractDOI(doi, apikey);
	    if (a != null) {
		abstracts.add(a);
	    }
        } else {
            System.err.println("You must supply either --retmax and --term for a search, or --list with a comma-separated list of PMIDs.");
            System.exit(1);
        }

        // remove abstracts that lack text
	int emptyCount = 0;
        List<Abstract> all = new ArrayList<>(abstracts); // avoid concurrent mod
        for (Abstract a : all) {
            if ((a.getText() == null) || (a.getText().length() == 0)) {
                abstracts.remove(a);
		emptyCount++;
            }
        }
	System.out.println("## Removed " + emptyCount + " empty abstracts.");

	// upsert the abstracts (only new ones if --update given)
	if (abstracts!=null && abstracts.size()>0) {
            OpenAi openai = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);
            upsertVectors(openai, pinecone, abstracts);
	} else {
	    System.out.println("## No abstracts were upserted.");
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
            // Add this Vector using PubMed-PMID as id
            vectors.add(Vector.newBuilder()
                        .setId(formPineconeId(a.getPMID()))
                        .setMetadata(metadata)
                        .addAllValues(floatEmbedding)
                        .build());
        }
        return vectors;
    }

    // form the Pinecone id from a PMID
    static String formPineconeId(String pmid) {
        return "PubMed-" + pmid;
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
