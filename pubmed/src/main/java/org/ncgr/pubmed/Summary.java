package org.ncgr.pubmed;

import org.ncgr.pubmed.xml.esummary.DocSum;
import org.ncgr.pubmed.xml.esummary.ERROR;
import org.ncgr.pubmed.xml.esummary.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the contents of a PubMed eSummaryResult DocSum.
 *
 * @author Sam Hokin
 */
public class Summary {

    public static String CHARSET = "UTF-8";

    // ERROR fields
    String error;

    // DocSum fields
    String pmid;
    String pubDate;
    String ePubDate;
    String source;
    String lastAuthor;
    String title;
    String volume;
    String issue;
    String pages;
    String nlmUniqueId;
    String issn;
    String essn;
    String recordStatus;
    String pubStatus;
    String doi;
    boolean hasAbstract;
    int pmcRefCount;
    String fullJournalName;
    String eLocationId;
    String so;

    // having trouble with these!
    // public List<String> authorList = new ArrayList<String>();
    // public List<String> langList = new ArrayList<String>();
    // public List<String> pubTypeList = new ArrayList<String>();
    // public Map<String,String> articleIds = new LinkedHashMap<String,String>();
    // public Map<String,String> history = new LinkedHashMap<String,String>();
    // public Map<String,String> references = new LinkedHashMap<String,String>();

    /**
     * Construct from a DocSum
     */ 
    public Summary(DocSum docSum) {
        pmid = docSum.getId();
        // PROBLEM: this only returns top-level Items, not those within a List!
        List<Item> items = docSum.getItem();
        for (Item item : items) {
            switch (item.getName()) {
            case "PubDate": pubDate = item.getvalue(); break;
            case "EPubDate": ePubDate = item.getvalue(); break;
            case "Source": source = item.getvalue(); break;
            case "AuthorList":
                break;
            case "LastAuthor": lastAuthor = item.getvalue(); break;
            case "Title": title = item.getvalue(); break;
            case "Volume": volume = item.getvalue(); break;
            case "Issue": issue = item.getvalue(); break;
            case "Pages": pages = item.getvalue(); break;
            case "LangList":
                break;
            case "NlmUniqueID": nlmUniqueId = item.getvalue(); break;
            case "ISSN": issn = item.getvalue(); break;
            case "ESSN": essn = item.getvalue(); break;
            case "PubTypeList":
                break;
            case "RecordStatus": recordStatus = item.getvalue(); break;
            case "PubStatus": pubStatus = item.getvalue(); break;
            case "ArticleIds": 
                break;
            case "DOI": doi = item.getvalue(); break;
            case "History":
                break;
            case "References":
                break;
            case "HasAbstract": hasAbstract = (item.getvalue().equals("1")); break;
            case "PmcRefCount": pmcRefCount = Integer.parseInt(item.getvalue()); break;
            case "FullJournalName": fullJournalName = item.getvalue(); break;
            case "ELocationID": eLocationId = item.getvalue(); break;
            case "SO": so = item.getvalue(); break;
            default:
                System.out.println(item.getName() + ":" + item.getvalue());
                break;
            }
        }
    }

    /**
     * Construct from an ERROR.
     */
    Summary(ERROR esummaryError) {
        error = esummaryError.getvalue();
    }

    /**
     * Construct from an org.ncgr.pubmed.xml.esearch.ERROR.
     */
    Summary(org.ncgr.pubmed.xml.esearch.ERROR esearchError) {
        error = esearchError.getvalue();
    }

    /**
     * Return true if this Summary is an ERROR.
     */
    public boolean isError() {
        return (error != null);
    }

    /**
     * Spit out a string representation of this summary.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("---\n");
        if (error!=null) {
            sb.append("ERROR:" + error);
        } else {
            sb.append("PMID:"+pmid+"\n");
            sb.append("PubDate:"+pubDate+"\n");
            sb.append("EPubDate:"+ePubDate+"\n");
            sb.append("Source:"+source+"\n");
            // for (String author : authorList) {
            //     sb.append("Author:"+author+"\n");
            // }
            sb.append("LastAuthor:"+lastAuthor+"\n");
            sb.append("Title:"+title+"\n");
            sb.append("Volume:"+volume+"\n");
            sb.append("Issue:"+issue+"\n");
            sb.append("Pages:"+pages+"\n");
            // for (String lang : langList) {
            //     sb.append("Lang:"+lang+"\n");
            // }
            sb.append("NlmUniqueID:"+nlmUniqueId+"\n");
            sb.append("ISSN:"+issn+"\n");
            sb.append("ESSN:"+essn+"\n");
            // for (String pubType : pubTypeList) {
            //     sb.append("PubType:"+pubType+"\n");
            // }
            sb.append("RecordStatus:"+recordStatus+"\n");
            sb.append("PubStatus:"+pubStatus+"\n");
            sb.append("DOI:"+doi+"\n");
            sb.append("HasAbstract:"+hasAbstract+"\n");
            sb.append("PmcRefCount:"+pmcRefCount+"\n");
            sb.append("FullJournalName:"+fullJournalName+"\n");
            sb.append("ELocationID:"+eLocationId+"\n");
            sb.append("SO:"+so+"\n");
        }
        return sb.toString();
    }

    // getters
    public String getPMID() { return pmid; }
    public String getPubDate() { return pubDate; }
    public String getEPubDate() { return ePubDate; }
    public String getSource() { return source; }
    public String getLastAuthor() { return lastAuthor; }
    public String getTitle() { return title; }
    public String getVolume() { return volume; }
    public String getIssue() { return issue; }
    public String getPages() { return pages; }
    public String getNlmUniqueId() { return nlmUniqueId; }
    public String getISSN() { return issn; }
    public String getESSN() { return essn; }
    public String getRecordStatus() { return recordStatus; }
    public String getPubStatus() { return pubStatus; }
    public String getDOI() { return doi; }
    public boolean hasAbstract() { return hasAbstract; }
    public int getPMCRefCount() { return pmcRefCount; }
    public String getFullJournalName() { return fullJournalName; }
    public String getELocationId() { return eLocationId; }
    public String getSO() { return so; }
    
}

