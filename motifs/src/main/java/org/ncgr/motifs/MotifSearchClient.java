package org.ncgr.motifs;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Front end for the MotifSearch servlet, which searches for known motifs (proteins) using a MEME-format database.
 *
 * @author Sam Hokin
 */
public class MotifSearchClient {

    String servletUri;
    String query;
    JSONObject json;

    /**
     * Construct and run the request against the given servlet URI for the given query.
     */
    public MotifSearchClient(String servletUri, String query) throws ClientProtocolException, IOException {
        this.servletUri = servletUri;
        this.query = query;
        json = new JSONObject(Request.Get(servletUri+"?query="+query).execute().returnContent().toString());
    }

    /**
     * Return the servlet URI that was used to create this instance.
     */
    public String getServletUri() {
        return servletUri;
    }

    /**
     * Return the query that was used to create this instance.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Return the full JSON response from the search.
     */
    public JSONObject getJSONObject() {
        return json;
    }
        
    /**
     * Return the JSON for the best hit (highest score) motif of this instance.
     */
    public JSONObject getBestHitJSONObject() {
        double maxScore = 0.0;
        JSONObject maxMotif = null;
        for (String key : json.keySet()) {
            JSONObject motif = json.getJSONObject(key);
            double score = motif.getDouble("score");
            if (score>maxScore) {
                maxScore = score;
                maxMotif = motif;
            }
        }
        return maxMotif;
    }

    /**
     * Command-line testing.
     */
    public static void main(String[] args) throws ClientProtocolException, IOException {
        // validate
        if (args.length!=2) {
            System.out.println("Usage: MotifSearchClient <MotifSearchServlet URI> <query motif>");
            System.exit(1);
        }
        String servletUri = args[0];
        String query = args[1];
        MotifSearchClient msc = new MotifSearchClient(servletUri, query);
        JSONObject maxMotif = msc.getBestHitJSONObject();
        System.out.println("query:\t\t"+query);
        System.out.println("motifLength:\t"+maxMotif.getInt("motifLength"));
        System.out.println("id:\t\t"+maxMotif.getInt("id"));
        System.out.println("name:\t\t"+maxMotif.getString("name"));
        System.out.println("score:\t\t"+maxMotif.getDouble("score")+" ("+maxMotif.getDouble("score")/maxMotif.getDouble("motifLength")+")");
        System.out.println("collection:\t"+maxMotif.getString("collection"));
        System.out.println("baseId:\t\t"+maxMotif.getString("baseId"));
        System.out.println("version:\t"+maxMotif.getInt("version"));
    }

}
