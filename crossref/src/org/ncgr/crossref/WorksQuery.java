package org.ncgr.crossref;

import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Query the CrossRef REST API for a work and return the closest match - either an exact match to the query or the first result (usually the same).
 */
public class WorksQuery {

    public static String WORKS_URL_ROOT = "https://api.crossref.org/works";

    String queryAuthor;
    String queryTitle;
    String queryDOI;
    URL worksUrl;

    String status;
    String messageType;
    String messageVersion;
    long messageTotalResults;
    
    JSONObject item;

    /**
     * Query an author and title.
     */
    public void queryAuthorTitle(String queryAuthor, String queryTitle) throws UnsupportedEncodingException, MalformedURLException, ParseException, IOException {
        this.queryAuthor = queryAuthor;
        this.queryTitle = queryTitle;
        this.worksUrl = new URL(WORKS_URL_ROOT+
                                "?rows=1" +
                                "&query.author="+URLEncoder.encode(queryAuthor,"UTF-8") +
                                "&query.title="+URLEncoder.encode(queryTitle,"UTF-8")
                                );
        query();
    }

    /**
     * Query a DOI.
     */
    public void queryDOI(String queryDOI) throws UnsupportedEncodingException, MalformedURLException, ParseException, IOException {
        this.queryDOI = queryDOI;
        this.worksUrl = new URL(WORKS_URL_ROOT+"/"+URLEncoder.encode(queryDOI,"UTF-8"));
        try {
            query();
        } catch (FileNotFoundException e) {
            status = "DOI NOT FOUND";
        }
    }

    /**
     * Execute query given a populated worksUrl field.
     */
    void query() throws ParseException, IOException, FileNotFoundException {
        InputStreamReader reader = new InputStreamReader(worksUrl.openStream());
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(reader);
        status = (String) response.get("status");
        messageType = (String) response.get("message-type");
        messageVersion = (String) response.get("message-version");
        JSONObject message = (JSONObject) response.get("message");
        if (messageType.equals("work")) {
            item = message;
        } else if (messageType.equals("work-list")) {
            messageTotalResults = (long)(Long) message.get("total-results");
            JSONArray items = (JSONArray) message.get("items");
            item = (JSONObject) items.get(0);
        } else {
            System.err.println("Can't process message of type:"+messageType);
            System.exit(1);
        }
    }
    
    /**
     * Testing, 1, 2, 3.
     */
    public static void main(String[] args) throws UnsupportedEncodingException, MalformedURLException, IOException, ParseException {

        String queryAuthor = null;
        String queryTitle = null;
        String queryDOI = null;
        
        // 2 args is author/title query; 1 arg is DOI query
        if (args.length==2) {
            if (args[0].length()>0 && args[1].length()>0) {
                queryAuthor = args[0];
                queryTitle = args[1];
            } else if (args[0].length()>0) {
                queryDOI = args[0];
            }
        } else if (args.length==1) {
            queryDOI = args[0];
        } else {
            // defaults for testing
            queryAuthor = "Perez-Vega";
            queryTitle = "Mapping of QTLs for Morpho-Agronomic and Seed Quality Traits in a RIL Population of Common Bean (Phaseolus vulgaris L.)";
        }

        WorksQuery wq = new WorksQuery();
        if (queryDOI!=null) {
            wq.queryDOI(queryDOI);
        } else if (queryAuthor!=null && queryTitle!=null) {
            wq.queryAuthorTitle(queryAuthor, queryTitle);
        } else {
            System.err.println("Error: neither DOI nor author/title provided.");
            System.exit(1);
        }

        System.out.println("status:\t"+wq.getStatus());
        if (wq.getStatus().equals("ok")) {
            System.out.println("message-type:\t"+wq.getMessageType());
            System.out.println("message-version:\t"+wq.getMessageVersion());
            System.out.println("message.total-results:\t"+wq.getMessageTotalResults());
            
            System.out.println("");
            if (wq.isTitleMatched()) System.out.println("******************************* TITLE MATCH! *******************************");
            System.out.println(wq.getTitle());
            
            for (Object authorObject : wq.getAuthors())  {
                JSONObject author = (JSONObject) authorObject;
                System.out.println(author.get("family")+","+author.get("given"));
            }
            
            System.out.println(wq.getDOI());
            System.out.println(wq.getPublisher());

            System.out.println(wq.getShortContainerTitle());
            System.out.println(wq.getContainerTitle());
            
            if (wq.getShortContainerTitle()!=null) {
                System.out.print(wq.getShortContainerTitle()+" ");
            } else if (wq.getContainerTitle()!=null) {
                System.out.print(wq.getContainerTitle()+" ");
            }
            System.out.print(wq.getVolume()+" [");
            System.out.print(wq.getIssue()+"], ");                    
            System.out.print(wq.getPage()+" (");
            System.out.println(wq.getIssueMonth()+"/"+wq.getIssueYear()+")");
            System.out.println(wq.getLinkUrl());
            System.out.println("ISSN: print="+wq.getPrintISSN()+" electronic="+wq.getElectronicISSN());
            System.out.println("score="+wq.getScore());
            if (wq.isTitleMatched()) System.out.println("****************************************************************************");
            System.out.println("");
        }

    }

    static String stringOrNull(Object o) {
        if (o==null) {
            return null;
        } else {
            return (String) o;
        }
    }

    public String getStatus() {
        return status;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public long getMessageTotalResults() {
        return messageTotalResults;
    }

    public String getQueryAuthor() {
        return queryAuthor;
    }

    public String getQueryTitle() {
        return queryTitle;
    }

    public String getTitle() {
        if (item.get("title")!=null) {
            JSONArray titles = (JSONArray) item.get("title");
            return stringOrNull(titles.get(0));
        } else {
            return null;
        }
    }
    
    public boolean isTitleMatched() {
        if (queryTitle!=null) {
            return queryTitle.toLowerCase().equals(getTitle().toLowerCase());
        } else {
            return false;
        }
    }
    
    public JSONArray getAuthors() {
        if (item.get("author")!=null) {
            JSONArray authors = (JSONArray) item.get("authors");
            return (JSONArray) item.get("author");
        } else {
            return null;
        }
    }
    
    public String getDOI() {
        return stringOrNull(item.get("DOI"));
    }
    
    public String getShortContainerTitle() {
        if (item.get("short-container-title")!=null) {
            JSONArray shortContainerTitles = (JSONArray) item.get("short-container-title");
            if (shortContainerTitles.size()>0) {
                return stringOrNull(shortContainerTitles.get(0));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public String getContainerTitle() {
        if (item.get("container-title")!=null) {
            JSONArray containerTitles = (JSONArray) item.get("container-title");
            if (containerTitles.size()>0) {
                return stringOrNull(containerTitles.get(0));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public String getVolume() {
        return stringOrNull(item.get("volume"));
    }
    
    public String getIssue() {
        return stringOrNull(item.get("issue"));

    }

    public String getPage() {
        return stringOrNull(item.get("page"));

    }

    public String getLinkUrl() {
        if (item.get("link")!=null) {
            JSONArray links = (JSONArray) item.get("link");
            JSONObject link = (JSONObject) links.get(0);
            return stringOrNull(link.get("URL"));
        } else {
            return null;
        }
    }

    public double getScore() {
        return (double)(Double) item.get("score");
    }

    public String getPrintISSN() {
        if (item.get("ISSN")!=null) {
            JSONArray issn = (JSONArray) item.get("ISSN");
            return stringOrNull(issn.get(0));
        } else {
            return null;
        }
    }
    public String getElectronicISSN() {
        if (item.get("ISSN")!=null) {
            JSONArray issn = (JSONArray) item.get("ISSN");
            if (issn.size()>1) {
                return stringOrNull(issn.get(1));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int getIssueYear() {
        if (item.get("issued")!=null) {
            JSONObject issued = (JSONObject) item.get("issued");
            JSONArray dateParts = (JSONArray) issued.get("date-parts");
            JSONArray parts = (JSONArray) dateParts.get(0);
            return (int)(long)(Long) parts.get(0);
        } else {
            return 0;
        }
    }
    public int getIssueMonth() {
        if (item.get("issued")!=null) {
            JSONObject issued = (JSONObject) item.get("issued");
            JSONArray dateParts = (JSONArray) issued.get("date-parts");
            JSONArray parts = (JSONArray) dateParts.get(0);
            if (parts.size()>1) {
                return (int)(long)(Long) parts.get(1);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    public int getIssueDay() {
        if (item.get("issued")!=null)  {
            JSONObject issued = (JSONObject) item.get("issued");
            JSONArray dateParts = (JSONArray) issued.get("date-parts");
            JSONArray parts = (JSONArray) dateParts.get(0);
            if (parts.size()>2) {
                return (int)(long)(Long) parts.get(2);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public String getPublisher() {
        return stringOrNull(item.get("publisher"));
    }

}
