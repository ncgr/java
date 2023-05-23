package org.ncgr.chatbot;

import org.ncgr.chatbot.openai.OpenAi;
import org.ncgr.chatbot.pinecone.Pinecone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import io.pinecone.PineconeException;
import io.pinecone.proto.ScoredVector;
import io.pinecone.proto.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command-line utility to dump vector data from a Pinecone index. We need OpenAi to form a query embedding.
 */
public class DataDumper {

    public static void main(String[] args) throws IOException, PineconeException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option indexOption = new Option("i", "index", true, "Pinecone index name");
        indexOption.setRequired(true);
        options.addOption(indexOption);

        Option termOption = new Option("t", "term", true, "search term for query");
        termOption.setRequired(false);
        options.addOption(termOption);

        Option idOption = new Option("id", "id", true, "id of a vector to fetch");
        idOption.setRequired(false);
        options.addOption(idOption);

        Option topkOption = new Option("topk", "topk", true, "Pinecone Top K value: maximum number of vectors to retrieve [" + Pinecone.TOP_K + "]");
        topkOption.setRequired(false);
        options.addOption(topkOption);

        Option filterOption = new Option("f", "filter", false, "filter the query on metadata given by --key and --value");
        filterOption.setRequired(false);
        options.addOption(filterOption);

        Option keyOption = new Option("k", "key", true, "key of the metadata for query filter or update");
        keyOption.setRequired(false);
        options.addOption(keyOption);

        Option valueOption = new Option("v", "value", true, "value of the metadata for query filter or update");
        valueOption.setRequired(false);
        options.addOption(valueOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("DataDumper", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("DataDumper", options);
            System.exit(1);
            return;
        }
        
        // get API keys and other environment parameters
        String openaiApiKey = System.getenv().get("OPENAI_API_KEY");
        String pineconeProjectName = System.getenv().get("PINECONE_PROJECT_NAME");
        String pineconeApiKey = System.getenv().get("PINECONE_API_KEY");
        String pineconeEnvironment = System.getenv().get("PINECONE_ENVIRONMENT");
        if (openaiApiKey==null || pineconeProjectName==null || pineconeApiKey==null || pineconeEnvironment==null) {
            System.err.println("You must set the environment variables: OPENAI_API_KEY, PINECONE_PROJECT_NAME, PINECONE_API_KEY, PINECONE_ENVIRONMENT");
            System.exit(1);
        }

        // required parameters
        String pineconeIndexName = cmd.getOptionValue("index");
        if (pineconeIndexName.trim().length() == 0) {
            System.err.println("You must supply a Pinecone index name with --index.");
            System.exit(1);
        }
	
        // default/optional parameters
        int topK = Pinecone.TOP_K;
        if (cmd.hasOption("topk")) topK = Integer.parseInt(cmd.getOptionValue("topk"));
        if (topK > 10000) {
            System.err.println("--topk must be 10000 or less.");
            System.exit(1);
        }

        // operations
        boolean doQuery = cmd.hasOption("term");
        boolean doFetch = cmd.hasOption("id");
        
        if (doFetch) {
            // create the Pinecone object and fetch a regular Vector for the given id
            Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
            List<String> idList = new ArrayList<>();
            idList.add(cmd.getOptionValue("id"));
            Map<String,Vector> vectorMap = pinecone.fetchVectors(idList);
            // output: we can't just output vectors because character encoding is fouled up; have to use metadata as extracted with Struct methods.
            for (Vector v : vectorMap.values()) {
		String id = v.getId();
		// HACK
		if (id.startsWith("PubMed-")) {
		    // do nothing
		} else if (id.startsWith("PubMed:")) {
		    id = id.replace("PubMed:", "PubMed-");
		} else if (id.startsWith("PubAg-")) {
		    // do nothing
		} else if (id.startsWith("PubAg:")) {
		    id = id.replace("PubAg:", "PubAg-");
		} else if (id.startsWith("PubAg")) {
		    // fix PubAg1234567
		    id = id.replace("PubAg", "PubAg-");
		} else {
		    id = "PubMed-" + id;
		}
		System.out.println("id:" + id);
		for (Float value : v.getValuesList()) {
		    System.out.println(String.valueOf(value));
		}
		Map<String,String> metadata = Pinecone.getMetadata(v);
		if (metadata.size() > 0) {
		    System.out.println("metadata");
		    for (String key : metadata.keySet()) {
			System.out.println(key + ":" + metadata.get(key));
		    }
		}
		System.out.println("");
	    }
        }

        if (doQuery) {
	    String term = cmd.getOptionValue("term");
	    if (term.trim().length() == 0) {
		System.err.println("You must supply a non-empty Pinecone query term with --term.");
		System.exit(1);
	    }
	    boolean addFilter = cmd.hasOption("filter");
	    if (addFilter && (!cmd.hasOption("key") || !cmd.hasOption("value"))) {
		System.err.println("--filter option requires metadata --key and --value parameters.");
		System.exit(1);
	    }
            // our scored vectors
            List<ScoredVector> vectors;
            // create an OpenAI object and get encoded query for term
            OpenAi openAi = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);
            List<Float> encodedQuery = openAi.getEncodedQuery(term);
            // create the Pinecone object and get the ScoredVectors with both values and metadata
            Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
            if (addFilter) {
                Struct filter = Pinecone.makeEqFilter(cmd.getOptionValue("key"), cmd.getOptionValue("value"));
                vectors = pinecone.getScoredVectorsWithFilter(encodedQuery, filter, topK, true, true);
            } else {
                vectors = pinecone.getScoredVectors(encodedQuery, topK, true, true);
            }
            // output: we can't just output vectors because character encoding is fouled up; have to use metadata as extracted with Struct methods.
	    // HACK
	    List<String> uniqueIds = new ArrayList<>();
            for (ScoredVector v : vectors) {
		String id = v.getId();
		// HACK
		if (id.startsWith("PubMed-")) {
		    // do nothing
		} else if (id.startsWith("PubAg-")) {
		    // do nothing
		} else if (id.startsWith("PubAg:")) {
		    id = id.replace("PubAg:", "PubAg-");
		} else if (id.startsWith("PubAg")) {
		    // fix PubAg1234567
		    id = id.replace("PubAg", "PubAg-");
		} else {
		    id = "PubMed-" + id;
		}
		if (!uniqueIds.contains(id)) {
		    uniqueIds.add(id);
		    System.out.println("id:" + id);
		    for (Float value : v.getValuesList()) {
			System.out.println(String.valueOf(value));
		    }
		    Map<String,String> metadata = Pinecone.getMetadata(v);
		    if (metadata.size() > 0) {
			System.out.println("metadata");
			for (String key : metadata.keySet()) {
			    System.out.println(key + ":" + metadata.get(key));
			}
		    }
		    System.out.println("");
		}
            }
        }
    }

}
