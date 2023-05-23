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

	// retrieve abstracts
	List<Abstract> abstracts = new ArrayList<>();
        if (cmd.hasOption("file")) {
            String filename = cmd.getOptionValue("file");
            abstracts = Abstract.load(filename);
        } else if (cmd.hasOption("term")) {
            String term = cmd.getOptionValue("term");
            abstracts = Pubag.searchAbstractOrTitleText(term, page, perpage, apikey);
        }

	// upsert our abstracts
	if (abstracts!=null && abstracts.size()>0) {
	    System.out.println("Found " + abstracts.size() + " total abstracts...");
	    OpenAi openai = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);
	    Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
	    upsertVectors(openai, pinecone, abstracts);
	} else {
	    System.out.println("No abstracts found.");
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
            String id = "PubAg-" + a.getId();
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
     * NOTE: we check that the DOI/title isn't already present in the index and only upload new DOIs or titles.
     */
    static void upsertVectors(OpenAi openai, Pinecone pinecone, List<Abstract> allAbstracts) {
	// reject abstracts that have DOI/title already present in index
	List<Float> encodedQuery = OpenAi.getTheEmbeddingAsFloats(); // "the" should cover most abstracts!
	List<Abstract> abstracts = new ArrayList<>();
	int rejectCount = 0;
	for (Abstract a : allAbstracts) {
	    String doi = a.getDOI();
	    String title = a.getTitle();
	    // DOI check
	    boolean doiFound = false;
	    if (doi != null) {
		Struct filter = Pinecone.makeEqFilter("DOI", doi);
		List<ScoredVector> scoredVectors = pinecone.getScoredVectorsWithFilter(encodedQuery, filter, 10, false, false);
		doiFound = scoredVectors.size() > 0;
	    }
	    // title check
	    boolean titleFound = false;
	    if (title != null) {
		Struct filter = Pinecone.makeEqFilter("title", title);
		List<ScoredVector> scoredVectors = pinecone.getScoredVectorsWithFilter(encodedQuery, filter, 10, false, false);
		titleFound = scoredVectors.size() > 0;
	    }
	    if (!doiFound && !titleFound) {
		    abstracts.add(a);
	    } else {
		rejectCount++;
	    }
	}
	if (rejectCount > 0) {
	    System.out.println("Rejected " + rejectCount + " abstracts which are already present in index.");
	}
	if (abstracts.size() > 0) {
	    // now upsert the new abstracts
	    int start = 0;
	    int end = Math.min(100, abstracts.size());
	    boolean hasMore = true;
	    while (hasMore) {
		List<Abstract> subList = abstracts.subList(start, end);
		// get the contexts
		List<String> contexts = getContexts(subList);
		// get the embeddings for these contexts
		List<Embedding> embeddings = openai.getEmbeddings(contexts);
		// form Vectors with metadata from these contexts and embeddings
		List<Vector> vectors = getVectors(embeddings, subList);
		// upsert the vectors to Pinecone
		pinecone.upsertVectors(vectors);
		System.out.println("Upserted "+vectors.size()+" abstract embedding vectors into Pinecone index.");
		// increment sub-list indexes
		start = Math.min(start + 100, abstracts.size());
		end = Math.min(end + 100, abstracts.size());
		hasMore = start < abstracts.size();
	    }
	    System.out.println("Upserted " + abstracts.size() + " abstracts to Pinecone index.");
        }
    }
}
