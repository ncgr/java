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
 * Command-line utility to retrieve and manipulate metadata in Pinecone vectors.
 *
 * Note: this method for updating is much slower than straight curl: around 54 updates per minute versus 400 updates per minute with curl.
 */
public class Metadata {

    public static void main(String[] args) throws IOException, PineconeException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option indexOption = new Option("i", "index", true, "Pinecone index");
        indexOption.setRequired(true);
        options.addOption(indexOption);

        Option topkOption = new Option("topk", "topk", true, "Pinecone Top K value: maximum number of contexts to retrieve [" + Pinecone.TOP_K + "]");
        topkOption.setRequired(false);
        options.addOption(topkOption);

        Option queryOption = new Option("q", "query", false, "perform a query");
        queryOption.setRequired(false);
        options.addOption(queryOption);
        
        Option filterOption = new Option("f", "filter", false, "filter a query on metadata given by --key and --value");
        filterOption.setRequired(false);
        options.addOption(filterOption);

        Option updateOption = new Option("u", "update", false, "update the metadata given by --key and --value for the vector given by --id");
        updateOption.setRequired(false);
        options.addOption(updateOption);

        Option idOption = new Option("id", "id", true, "id of a vector");
        idOption.setRequired(false);
        options.addOption(idOption);

        Option termOption = new Option("t", "term", true, "search term for query");
        termOption.setRequired(false);
        options.addOption(termOption);

        Option keyOption = new Option("k", "key", true, "key of the metadata for query filter or update");
        keyOption.setRequired(false);
        options.addOption(keyOption);

        Option valueOption = new Option("v", "value", true, "value of the metadata for query filter or update");
        valueOption.setRequired(false);
        options.addOption(valueOption);

        Option missingKeyOption = new Option("mk", "missingkey", true, "return vectors that are missing this metadata key");
        missingKeyOption.setRequired(false);
        options.addOption(missingKeyOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Metadata", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Metadata", options);
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
            System.err.println("topk must be 10000 or less.");
            System.exit(1);
        }
        String term = "the"; // matches any English abstract
        if (cmd.hasOption("term")) term = cmd.getOptionValue("term");
        
        boolean addFilter = cmd.hasOption("filter");
        if (addFilter && (!cmd.hasOption("key") || !cmd.hasOption("value"))) {
            System.err.println("--filter option requires metadata --key and --value parameters.");
            System.exit(1);
        }

        boolean checkMissingKey = cmd.hasOption("missingkey");
        
        // operations
        boolean doQuery = cmd.hasOption("query");
        boolean doUpdate = cmd.hasOption("update");
        boolean doFetch = cmd.hasOption("id") && !cmd.hasOption("update") && !cmd.hasOption("query");
        
        // validation
        if (doUpdate && (!cmd.hasOption("id") || !cmd.hasOption("key") || !cmd.hasOption("value"))) {
            System.err.println("--update option requires --id and metadata --key and --value parameters.");
            System.exit(1);
        }

        if (doFetch) {
            // create the Pinecone object and fetch a regular Vector for the given id, output its metadata
            Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
            List<String> idList = new ArrayList<>();
            idList.add(cmd.getOptionValue("id"));
            Map<String,Vector> vectorMap = pinecone.fetchVectors(idList);
            for (Vector v : vectorMap.values()) {
                boolean output = true;
                if (checkMissingKey) output = (Pinecone.getMetadataValue(v, cmd.getOptionValue("missingkey")) == null);
                if (output) {
                    System.out.println("--- " + v.getId());
                    Map<String,Value> fieldsMap = v.getMetadata().getFieldsMap();
                    for (String k : fieldsMap.keySet()) {
                        System.out.println(k + ": " + fieldsMap.get(k).getStringValue());
                    }
                }
            }
        }

        if (doUpdate) {
            // create the Pinecone object and get the ScoredVectors with metadata
            Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
            pinecone.updateVector(cmd.getOptionValue("id"), cmd.getOptionValue("key"), cmd.getOptionValue("value"));
        }

        if (doQuery) {
            // our vectors
            List<ScoredVector> vectors;
            // create an OpenAI object and get encoded query from it
            OpenAi openAi = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);
            List<Float> encodedQuery = openAi.getEncodedQuery(term);
            // create the Pinecone object and get the ScoredVectors with metadata
            boolean includeValues = false;
            boolean includeMetadata = true;
            Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);
            if (addFilter) {
                Struct filter = Pinecone.makeEqFilter(cmd.getOptionValue("key"), cmd.getOptionValue("value"));
                vectors = pinecone.getScoredVectorsWithFilter(encodedQuery, filter, topK, includeValues, includeMetadata);
            } else {
                vectors = pinecone.getScoredVectors(encodedQuery, topK, includeValues, includeMetadata);
            }

            // quietly exit if no vectors returned
            if (vectors.size()==0) System.exit(0);
            
            /////////////////////////////////////////////////////////////////////////////////////////////////////////
            // TEMP - purge duplicate vectors with the same DOI, favoring source=PubMed
            List<String> doiDupes = new ArrayList<>();
            Set<String> doiSet = new HashSet<>();
            for (ScoredVector vector : vectors) {
                String doi = Pinecone.getMetadataValue(vector, "DOI");
                if (doi != null) {
                    if (doiSet.contains(doi)) {
                        doiDupes.add(doi);
                    } else {
                        doiSet.add(doi);
                    }
                }
            }
            // spin through the Vectors again and delete the ones with DOI in doiDupes that aren't source=PubMed
            List<String> idsToDelete = new ArrayList<>();
            for (ScoredVector vector : vectors) {
                String doi = Pinecone.getMetadataValue(vector, "DOI");
                String source = Pinecone.getMetadataValue(vector, "source");
                if (doiDupes.contains(doi) && (source == null || !source.equals("PubMed"))) {
                    idsToDelete.add(vector.getId());
                }
            }
            if (idsToDelete.size() > 0) {
                pinecone.deleteVectors(idsToDelete);
                System.out.println("### DELETED " + idsToDelete.size() + " duplicate vectors.");
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////

            // query output
            int count = 0;
            for (ScoredVector v : vectors) {
                boolean output = true;
                if (checkMissingKey) output = (Pinecone.getMetadataValue(v, cmd.getOptionValue("missingkey")) == null);
                if (output) {
                    System.out.println("--- [" + ++count + "] " + v.getId());
                    Map<String,Value> fieldsMap = v.getMetadata().getFieldsMap();
                    for (String k : fieldsMap.keySet()) {
                        System.out.println(k + ": " + fieldsMap.get(k).getStringValue());
                    }
                }
            }
        }

    }
}
