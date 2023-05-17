package org.ncgr.chatbot;

import org.ncgr.chatbot.openai.OpenAi;
import org.ncgr.chatbot.pinecone.Pinecone;

import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * For lack of a better word, this class provides a command-line utility to "clean" Vector metadata: remove a specified key-value pair.
 * If an id is provided, then that Vector will have the given key removed, otherwise a query is performed with an optional filter
 * on a particular value of the key.
 */
public class Cleaner {

    public static void main(String[] args) throws IOException, PineconeException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option indexOption = new Option("i", "index", true, "Pinecone index");
        indexOption.setRequired(true);
        options.addOption(indexOption);

        Option keyOption = new Option("k", "key", true, "metadata key to be removed");
        keyOption.setRequired(true);
        options.addOption(keyOption);

        Option topkOption = new Option("topk", "topk", true, "maximum number of vectors to retrieve in a query [" + Pinecone.TOP_K + "]");
        topkOption.setRequired(false);
        options.addOption(topkOption);

        Option idsOption = new Option("ids", "ids", true, "comma-separated list of vector ids");
        idsOption.setRequired(false);
        options.addOption(idsOption);

        Option valueOption = new Option("v", "value", true, "value of the metadata item given by --key to restrict which are removed");
        valueOption.setRequired(false);
        options.addOption(valueOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Cleaner", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Cleaner", options);
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
        String key = cmd.getOptionValue("key");
        if (key.trim().length() == 0) {
            System.err.println("You must supply a metadata key to be removed with --key.");
            System.exit(1);
        }
        
        // default/optional parameters
        int topK = Pinecone.TOP_K;
        if (cmd.hasOption("topk")) topK = Integer.parseInt(cmd.getOptionValue("topk"));
        if (topK > 10000) {
            System.err.println("topk must be 10000 or less.");
            System.exit(1);
        }
        String value = null;
        if (cmd.hasOption("value")) value = cmd.getOptionValue("value");
        String ids = null;
        if (cmd.hasOption("ids")) ids = cmd.getOptionValue("ids");
        
        // create the Pinecone object
        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);

        // we get ScoredVector from queries
        List<ScoredVector> scoredVectors = new ArrayList<>();
        // we get plain Vector from fetch
        List<Vector> vectors = new ArrayList();
        
        if (ids == null) {
            // create an OpenAI object
            OpenAi openAi = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);
            // get encoded query from OpenAI using the search term "the" (which hopefully covers all Vectors)
            List<Float> encodedQuery = openAi.getEncodedQuery("the");
            // query ScoredVectors including both values and metadata
            if (value != null) {
                // add a filter on key:value
                Struct filter = Pinecone.makeEqFilter(key, value);
                scoredVectors = pinecone.getScoredVectorsWithFilter(encodedQuery, filter, topK, true, true);
            } else {
                // don't filter
                scoredVectors = pinecone.getScoredVectors(encodedQuery, topK, true, true);
            }
        } else {
            // fetch vectors by id
            List<String> idList = Arrays.asList(ids.split(","));
            Map<String,Vector> vectorMap = pinecone.fetchVectors(idList);
            for (Vector v : vectorMap.values()) {
                vectors.add(v);
            }
        }

        // quietly exit if no vectors returned of either type
        if (scoredVectors.size() == 0 && vectors.size() == 0) System.exit(0);
            
        // run through the vectors of either type, creating new Vectors with new metadata
        // only include vectors that contain the key to be removed
        List<Vector> newVectors = new ArrayList<>();
        for (ScoredVector v : scoredVectors) {
            if (Pinecone.getMetadataValue(v, key) != null) {
                System.out.println(v.getId());
                Map<String,Value> fieldsMap = v.getMetadata().getFieldsMap();
                Struct.Builder metadataBuilder = Struct.newBuilder();
                for (String k : fieldsMap.keySet()) {
                    if (!k.equals(key)) {
                        String val = fieldsMap.get(k).getStringValue();
                        metadataBuilder = metadataBuilder.putFields(k, Value.newBuilder().setStringValue(val).build());
                    }
                }
                Struct metadata = metadataBuilder.build();
                newVectors.add(Vector.newBuilder()
                               .setId(v.getId())
                               .addAllValues(v.getValuesList())
                               .setMetadata(metadata)
                               .build());
            }
        }
        for (Vector v : vectors) {
            if (Pinecone.getMetadataValue(v, key) != null) {
                System.out.println(v.getId());
                Map<String,Value> fieldsMap = v.getMetadata().getFieldsMap();
                Struct.Builder metadataBuilder = Struct.newBuilder();
                for (String k : fieldsMap.keySet()) {
                    if (!k.equals(key)) {
                        String val = fieldsMap.get(k).getStringValue();
                        metadataBuilder = metadataBuilder.putFields(k, Value.newBuilder().setStringValue(val).build());
                    }
                }
                Struct metadata = metadataBuilder.build();
                newVectors.add(Vector.newBuilder()
                               .setId(v.getId())
                               .addAllValues(v.getValuesList())
                               .setMetadata(metadata)
                               .build());
            }
        }

        // do the upsert
        System.out.println("Upserting " + newVectors.size() + " replacement vectors.");
        pinecone.upsertVectors(newVectors);
    }

}
