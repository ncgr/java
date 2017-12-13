package org.ncgr.crossref;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.ItemList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

/**
 * Query the CrossRef REST API for a work and return the closest match - either an exact match to the query or the first result (usually the same).
 */
public class WorksQuery {

    public static String WORKS_URL_ROOT = "https://api.crossref.org/works";

    static String TEST_AUTHOR = "Perez-Vega";
    static String TEST_TITLE = "Mapping of QTLs for Morpho-Agronomic and Seed Quality Traits in a RIL Population of Common Bean (Phaseolus vulgaris L.)";

    /**
     * Testing, 1, 2, 3.
     */
    public static void main(String[] args) {

        String queryAuthor = TEST_AUTHOR;
        String queryTitle = TEST_TITLE;
        if (args.length==2 && args[0].length()>0 && args[1].length()>0) {
            queryAuthor = args[0];
            queryTitle = args[1];
        } else if (args.length!=0) {
            System.out.println("Usage: WorksQuery [author] [title]");
            System.out.println("args.length="+args.length);
            System.exit(0);
        }

        try {
            
            URL url = new URL(WORKS_URL_ROOT+
                              "?query.author="+URLEncoder.encode(queryAuthor,"UTF-8") +
                              "&query.title="+URLEncoder.encode(queryTitle,"UTF-8")
                              );
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JSONParser parser = new JSONParser();

            JSONObject response = (JSONObject) parser.parse(reader);
            System.out.println("status:\t"+response.get("status"));
            System.out.println("message-type:\t"+response.get("message-type"));
            System.out.println("message-version:\t"+response.get("message-version"));

            JSONObject message = (JSONObject) response.get("message");
            System.out.println("message.total-results:\t"+message.get("total-results"));

            JSONArray items = (JSONArray) message.get("items");
            for (Object itemObject : items) {

                try {

                    JSONObject item = (JSONObject) itemObject;
                    
                    JSONArray titles = (JSONArray) item.get("title");
                    String title = stringOrNull(titles.get(0));
                    System.out.println("");
                    boolean matched = queryTitle.toLowerCase().equals(title.toLowerCase());
                    if (matched) System.out.println("******************************* MATCH! *******************************");
                    System.out.println(title);
                    
                    JSONArray authors = (JSONArray) item.get("author");
                    for (Object authorObject : authors)  {
                        JSONObject author = (JSONObject) authorObject;
                        System.out.println(author.get("family")+","+author.get("given"));
                    }
                    
                    String doi = stringOrNull(item.get("DOI"));
                    System.out.println(doi);

                    String shortContainerTitle = null;
                    if (item.get("short-container-title")!=null) {
                        JSONArray shortContainerTitles = (JSONArray) item.get("short-container-title");
                        shortContainerTitle = stringOrNull(shortContainerTitles.get(0));
                    }
                    String containerTitle = null;
                    if (item.get("container-title")!=null) {
                        JSONArray containerTitles = (JSONArray) item.get("container-title");
                        containerTitle = stringOrNull(containerTitles.get(0));
                    }
                    if (shortContainerTitle!=null) {
                        System.out.print(shortContainerTitle+" ");
                    } else if (containerTitle!=null) {
                        System.out.print(containerTitle+" ");
                    }
                    String volume = stringOrNull(item.get("volume"));
                    System.out.print(volume+" (");
                    String issue = stringOrNull(item.get("issue"));
                    System.out.print(issue+"), ");                    
                    String page = stringOrNull(item.get("page"));
                    System.out.println(page);
                    
                    String linkUrl = stringOrNull(item.get("URL"));
                    System.out.println(linkUrl);

                    double score = (double)(Double) item.get("score");
                    System.out.println("score="+score);
                    
                    if (matched) System.out.println("**********************************************************************");
                    System.out.println("");

                } catch (Exception e2) {
                    System.out.println(e2);
                }

            }

        } catch (Exception e1) {
            System.err.println(e1);
        }

    }

    static String stringOrNull(Object o) {
        if (o==null) {
            return null;
        } else {
            return (String) o;
        }
    }

}
