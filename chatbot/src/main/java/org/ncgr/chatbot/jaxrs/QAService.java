package org.ncgr.chatbot.jaxrs;

import org.ncgr.chatbot.QueryAnswer;
import org.ncgr.chatbot.openai.OpenAi;
import org.ncgr.chatbot.pinecone.Pinecone;

import java.util.List;

import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Handles a chatbot question/answer session. Only GET is implemented.
 */
@Path("/")
public class QAService {

    @Context ServletContext context;

    /**
     * GET ?question=this+is+my+question
     *
     * Sends a URI-encoded question to the chatbot and and returns the answer.
     *
     * @param question the URI-encoded question
     * @return the chatbot answer, or an error response, in JSON
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAnswer(@QueryParam("question") String question,
                              @QueryParam("top_k") String topKString,
                              @QueryParam("temperature") String temperatureString,
                              @QueryParam("frequency_penalty") String frequencyPenaltyString,
                              @QueryParam("presence_penalty") String presencePenaltyString,
                              @QueryParam("show_diagnostics") String showDiagnosticsString,
                              @QueryParam("show_ids") String showIDsString,
                              @QueryParam("show_dois") String showDOIsString,
                              @QueryParam("show_pmids") String showPMIDsString,
                              @QueryParam("show_pubagids") String showPubAgIDsString
                              ) {
        
        if (question==null || question.length()<10) {
            return Response
                .status(500)
                .entity("You must include a question query string that is at least 10 characters long.").build();
        }

        // defaults
        int topK = Pinecone.TOP_K;
        double temperature = OpenAi.TEMPERATURE;
        double frequencyPenalty = OpenAi.FREQUENCY_PENALTY;
        double presencePenalty = OpenAi.PRESENCE_PENALTY;

        // optional overrides
        if (topKString != null) topK = Integer.parseInt(topKString);
        if (temperatureString != null) temperature = Double.parseDouble(temperatureString);
        if (frequencyPenaltyString != null) frequencyPenalty = Double.parseDouble(frequencyPenaltyString);
        if (presencePenaltyString != null) presencePenalty = Double.parseDouble(presencePenaltyString);
        boolean showDiagnostics = (showDiagnosticsString != null);
        boolean showIDs = (showIDsString != null);
        boolean showDOIs = (showDOIsString != null);
        boolean showPMIDs = (showPMIDsString != null);
        boolean showPubAgIDs = (showPubAgIDsString != null);

        // get API keys from ServletContext
        String openaiApiKey = context.getInitParameter("openai.apikey");
        String pineconeProjectName = context.getInitParameter("pinecone.projectname");
        String pineconeApiKey = context.getInitParameter("pinecone.apikey");
        String pineconeEnvironment = context.getInitParameter("pinecone.environment");
        String pineconeIndexName = context.getInitParameter("pinecone.indexname");
        
        if (openaiApiKey==null || pineconeProjectName==null || pineconeApiKey==null || pineconeEnvironment==null || pineconeIndexName==null) {
            return Response
                .status(500)
                .entity("You must set openai.apikey, pinecone.projectname, pinecone.apikey, pinecone.environment, pinecone.indexname in web.xml.").build();
        }

        try {
            String answer = QueryAnswer.getAnswer(openaiApiKey,
                                                  pineconeProjectName, pineconeApiKey, pineconeEnvironment, pineconeIndexName,
                                                  question,
                                                  topK, temperature, frequencyPenalty, presencePenalty,
                                                  showDiagnostics, showIDs, showDOIs, showPMIDs, showPubAgIDs);
            return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", 
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity(answer).build();
        } catch (Exception ex) {
            return Response
                .status(500)
                .entity(ex.getMessage()).build();
        }
    }
}
