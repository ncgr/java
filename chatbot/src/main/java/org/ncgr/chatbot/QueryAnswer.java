package org.ncgr.chatbot;

import org.ncgr.chatbot.openai.OpenAi;
import org.ncgr.chatbot.pinecone.Pinecone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Value;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;

import io.pinecone.PineconeException;
import io.pinecone.proto.ScoredVector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class to query OpenAI using embeddings and vectors from Pinecone.
 */
public class QueryAnswer {
    
    /**
     * Get a chat completion for a given question.
     *
     * @param openaiApiKey the OpenAI API key
     * @param pineconeProjectName the Pinecone project name associated with the index
     * @param pineconeApiKey the Pinecone API key for accessing the index
     * @param pineconeEnvironment the Pinecone environment associated with the index
     * @param pineconeIndexName the name of the Pinecone index
     * @param question the question with which to form the prompt
     * @param topK the Pinecone top_k parameter determining the number of returned matching Vectors
     * @param temperature the OpenAI temperature parameter, affecting the distribution of generated chat completions
     * @param frequencyPenalty the OpenAI frequency_penalty parameter affecting repetitiveness
     * @param presencePenalty the OpenAI presence_penalty parameter affecting repetitiveness
     * @param showDiagnostics flag to append OpenAI diagnostics below the response
     * @param showVectorIDs flag to append the matching Pinecone vector ids to the response
     * @param showVectorDOIs flag to append the matching Pinecone vector metadata DOIs to the response
     * @return the string response from OpenAI
     */
    public static String getAnswer(String openaiApiKey,
                                   String pineconeProjectName, String pineconeApiKey, String pineconeEnvironment, String pineconeIndexName,
                                   String question,
                                   int topK, double temperature, double frequencyPenalty, double presencePenalty,
                                   boolean showDiagnostics, boolean showVectorIDs, boolean showVectorDOIs, boolean showVectorPMIDs, boolean showVectorPubAgIDs) {

        // create the OpenAI object
        OpenAi openAi = new OpenAi(openaiApiKey, OpenAi.TIMEOUT_SECONDS);

        // get the encoded query from OpenAI
        List<Float> encodedQuery = openAi.getEncodedQuery(question);

        // create the Pinecone object.
        Pinecone pinecone = new Pinecone(pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName, Pinecone.SERVER_SIDE_TIMEOUT_SEC);

        // get the matching vectors from Pinecone without values, but including metadata
        List<ScoredVector> vectors = pinecone.getScoredVectors(encodedQuery, topK, false, true);

        // get the contexts for the matching vectors
        List<String> contexts = pinecone.getAbstractContexts(vectors);

        if (contexts.size()>0) {
            // get the OpenAI chat completion request for our contexts and question
            ChatCompletionRequest request = openAi.getChatCompletionRequest(contexts, question, temperature, frequencyPenalty, presencePenalty);
            // and the get the result from the server (hopefully)
            try {
                ChatCompletionResult result = openAi.getChatCompletionResult(request);
                // build the answer with diagnostics
                String answer = "";
                String finishReason = "";
                List<ChatCompletionChoice> choices = result.getChoices();
                for (ChatCompletionChoice choice : choices) {
                    answer += choice.getMessage().getContent().trim();
                    finishReason = choice.getFinishReason();
                }
                if (showVectorIDs) {
                    answer += "\n---\n";
                    List<String> ids = new ArrayList<>();
                    for (ScoredVector vector : vectors) {
                        ids.add(vector.getId());
                    }
                    answer += "IDs: " + ids;
                }
                if (showVectorDOIs) {
                    answer += "\n---\n";
                    List<String> dois = new ArrayList<>();
                    for (ScoredVector vector : vectors) {
                        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
                        if (fieldsMap.get("DOI") != null) dois.add(fieldsMap.get("DOI").getStringValue());
                    }
                    answer += "DOIs: " + dois;
                }
                if (showVectorPMIDs) {
                    answer += "\n---\n";
                    List<String> pmids = new ArrayList<>();
                    for (ScoredVector vector : vectors) {
                        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
                        if (fieldsMap.get("PMID") != null) pmids.add(fieldsMap.get("PMID").getStringValue());
                    }
                    answer += "PubMed: " + pmids;
                }
                if (showVectorPubAgIDs) {
                    answer += "\n---\n";
                    List<String> pubagids = new ArrayList<>();
                    for (ScoredVector vector : vectors) {
                        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
                        if (fieldsMap.get("PubAgID") != null) pubagids.add(fieldsMap.get("PubAgID").getStringValue());
                    }
                    answer += "PubAg: " + pubagids;
                }
                if (showDiagnostics) {
                    answer += "\n---\n";
                    answer += "Finish reason: " + finishReason + "\n";
                    answer += "Prompt tokens: " + result.getUsage().getPromptTokens() + "\n";
                    answer += "Completion tokens: " + result.getUsage().getCompletionTokens() + "\n";
                    answer += "Total tokens: " + result.getUsage().getTotalTokens();
                }
                // return
                return answer;
            } catch (OpenAiHttpException ex) {
                return ex.getMessage();
            }
        } else {
            return "No contexts were supplied from Pinecone for the OpenAI chat completion.";
        }
    }


    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws IOException, PineconeException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option indexOption = new Option("i", "index", true, "Pinecone index");
        indexOption.setRequired(true);
        options.addOption(indexOption);

        Option topkOption = new Option("topk", "topk", true, "Pinecone Top K: maximum number of contexts to retrieve [" + Pinecone.TOP_K + "]");
        topkOption.setRequired(false);
        options.addOption(topkOption);

        Option temperatureOption = new Option("t", "temperature", true, "OpenAi temperature [0.0,2.0]: larger means more random completion [" + OpenAi.TEMPERATURE + "]");
        temperatureOption.setRequired(false);
        options.addOption(temperatureOption);
        
        Option frequencyPenaltyOption = new Option("f", "freqpenalty", true, "OpenAi frequency penalty: larger reduces redundancy [" + OpenAi.FREQUENCY_PENALTY+ "]");
        frequencyPenaltyOption.setRequired(false);
        options.addOption(frequencyPenaltyOption);

        Option presencePenaltyOption = new Option("p", "prespenalty", true, "OpenAi presence penalty: larger reduces redundancy [" + OpenAi.PRESENCE_PENALTY + "]");
        presencePenaltyOption.setRequired(false);
        options.addOption(presencePenaltyOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("QueryAnswer", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("QueryAnswer", options);
            System.exit(1);
            return;
        }
        
        // get keys from system environment
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
        if (pineconeIndexName.trim().length()==0) {
            System.err.println("You must supply a Pinecone index name.");
            System.exit(1);
        }

        // default or optional parameters
        int topK = Pinecone.TOP_K; if (cmd.hasOption("topk")) topK = Integer.parseInt(cmd.getOptionValue("topk"));
        double temperature = OpenAi.TEMPERATURE; if (cmd.hasOption("temperature")) temperature = Double.parseDouble(cmd.getOptionValue("temperature"));
        double frequencyPenalty = OpenAi.FREQUENCY_PENALTY; if (cmd.hasOption("freqpenalty")) frequencyPenalty = Double.parseDouble(cmd.getOptionValue("freqpenalty"));
        double presencePenalty = OpenAi.PRESENCE_PENALTY; if (cmd.hasOption("prespenalty")) presencePenalty = Double.parseDouble(cmd.getOptionValue("prespenalty"));
        
        // Enter the query using BufferReader and readLine
        System.out.println("Query:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String query = reader.readLine();

        // this can take a while
        System.out.println("Answer:");
        String answer = getAnswer(openaiApiKey,
                                  pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName,
                                  query,
                                  topK, temperature, frequencyPenalty, presencePenalty,
                                  true, true, true, true, true);
        System.out.println(answer);
    }

}
