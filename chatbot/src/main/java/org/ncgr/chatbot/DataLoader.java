package org.ncgr.chatbot;

import org.ncgr.chatbot.pinecone.Pinecone;

import java.io.BufferedReader;
import java.io.FileReader;
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

import org.apache.commons.text.StringEscapeUtils;

/**
 * Command-line utility to upsert vector data from a file into a Pinecone index.
 * (This class doesn't use OpenAi but it is located in this package since it
 * pairs with DataDumper, which does.)
 */
public class DataLoader {

    public static void main(String[] args) throws IOException, PineconeException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option indexOption = new Option("i", "index", true, "Pinecone index name");
        indexOption.setRequired(true);
        options.addOption(indexOption);

	Option fileOption = new Option("f", "file", true, "Name of file containing vector data and metadata to be loaded into Pinecone.");
	fileOption.setRequired(true);
	options.addOption(fileOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("DataLoader", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("DataLoader", options);
            System.exit(1);
            return;
        }
        
        // get API keys and other environment parameters
        String pineconeProjectName = System.getenv().get("PINECONE_PROJECT_NAME");
        String pineconeApiKey = System.getenv().get("PINECONE_API_KEY");
        String pineconeEnvironment = System.getenv().get("PINECONE_ENVIRONMENT");
        if (pineconeProjectName==null || pineconeApiKey==null || pineconeEnvironment==null) {
            System.err.println("You must set the environment variables: PINECONE_PROJECT_NAME, PINECONE_API_KEY, PINECONE_ENVIRONMENT");
            System.exit(1);
        }

        // required parameters
        String pineconeIndexName = cmd.getOptionValue("index");
        if (pineconeIndexName.trim().length() == 0) {
            System.err.println("You must supply a Pinecone index name with -i or --index.");
            System.exit(1);
        }
	String filename = cmd.getOptionValue("file");
        if (filename.trim().length() == 0) {
            System.err.println("You must supply a file name with -f or --file.");
            System.exit(1);
        }
	
	// load the vectors into memory from the file
	BufferedReader reader = new BufferedReader(new FileReader(filename));
	String line;
	Vector.Builder vectorBuilder = null;
	boolean inValues = false;
	boolean inMetadata = false;
	String id = null;
	List<Float> values = new ArrayList<>();
	Map<String,String> metadataMap = new HashMap<>();
	List<Vector> vectors = new ArrayList<>();
	while ((line = reader.readLine()) != null) {
	    if (line.startsWith("id:")) {
		id = getValue(line);
		// new vector
		vectorBuilder = Vector.newBuilder();
		vectorBuilder.setId(id);
		values = new ArrayList<>();
		inValues = true;
		inMetadata = false;
	    } else if (line.startsWith("metadata")) {
		// start metadata block
		metadataMap = new HashMap<>();
		inValues = false;
		inMetadata = true;
	    } else if (line.trim().length() == 0) {
		// end of vector
		vectorBuilder.addAllValues(values);
		if (metadataMap.size() > 0) {
		    // HACK 1 - fix PubAg vectors that don't have PubAgID metadata
		    if (id.startsWith("PubAg-") && !metadataMap.containsKey("PubAgID")) {
			String[] parts = id.split("PubAg-");
			metadataMap.put("PubAgID", parts[1]);
		    }
		    // HACK 2 - add source metadata if missing
		    if (!metadataMap.containsKey("source")) {
			if (id.startsWith("PubMed")) {
			    metadataMap.put("source", "PubMed");
			} else if (id.startsWith("PubAg")) {
			    metadataMap.put("source", "PubAg");
			}
		    }
		    // form metadata from metadataMap
		    Struct.Builder metadataBuilder = Struct.newBuilder();
		    for (String key : metadataMap.keySet()) {
			metadataBuilder.putFields(key, Value.newBuilder().setStringValue(metadataMap.get(key)).build());
		    }
		    vectorBuilder.setMetadata(metadataBuilder.build());
		}
		vectors.add(vectorBuilder.build());
		// reset
		id = null;
		inValues = false;
		inMetadata = false;
	    } else if (inValues) {
		// add this value
		values.add(Float.parseFloat(line));
	    } else if (inMetadata) {
		// add this key:value
		metadataMap.put(getKey(line), getValue(line));
	    }
	}
	// upsert vectors
	Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
	pinecone.upsertVectors(vectors);
	System.out.println("Upserted " + vectors.size() + " vectors to Pinecone index " + pineconeIndexName + ".");
    }

    /**
     * Parse the key from a key:value string
     */
    static String getKey(String line) {
	String[] parts = line.split(":");
	return parts[0];
    }
    
    /**
     * Parse the value from a key:value string
     * id:PubAg:1234567
     */
    static String getValue(String line) {
	String[] parts = line.split(":");
	String value = parts[1];
	for (int i=2; i<parts.length; i++) {
	    value += ":" + parts[i];
	}
	return value;
    }

}
