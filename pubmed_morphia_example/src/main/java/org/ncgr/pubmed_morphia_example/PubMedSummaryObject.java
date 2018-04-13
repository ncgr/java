package org.ncgr.pubmed_morphia_example;

import org.ncgr.pubmed.PubMedSummary;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Encapsulates a PubMed eSummaryResult in an object that gets stored in MongoDB using Morphia.
 *
 * TODO: put authors into their own table with a Reference.
 */
@Entity("PubMedSummaries")
@Indexes(@Index(value = "title", fields = @Field("title")))
public class PubMedSummaryObject {

    // eSummaryResult/DocSum fields
    @Id
    private Long id;

    private String pubDate;
    private String ePubDate;
    private String source;
    private String lastAuthor;
    private String title;
    private String volume;
    private String issue;
    private String pages;
    private String nlmUniqueId;
    private String issn;
    private String essn;
    private String recordStatus;
    private String pubStatus;
    private String doi;
    private Boolean hasAbstract;
    private Integer pmcRefCount;
    private String fullJournalName;
    private String eLocationId;
    private String so;

    private List<String> authorList;
    private List<String> langList;
    private List<String> pubTypeList;

    private Map<String,String> articleIds;
    private Map<String,String> history;
    private Map<String,String> references;

    /**
     * Default constructor, used by Morphia to instantiate. Somehow. Since there are no setters. Magic.
     */
    public PubMedSummaryObject() {
    }

    /**
     * Instantiate from a (hopefully) populated org.ncgr.pubmed.PubMedSummary.
     */
    public PubMedSummaryObject(PubMedSummary pms) {
        this.id = new Long(pms.id);
        this.pubDate = pms.pubDate;
        this.ePubDate = pms.ePubDate;
        this.source = pms.source;
        this.lastAuthor = pms.lastAuthor;
        this.title = pms.title;
        this.volume = pms.volume;
        this.issue = pms.issue;
        this.pages = pms.pages;
        this.nlmUniqueId = pms.nlmUniqueId;
        this.issn = pms.issn;
        this.essn = pms.essn;
        this.recordStatus = pms.recordStatus;
        this.pubStatus = pms.pubStatus;
        this.doi = pms.doi;
        this.hasAbstract = new Boolean(pms.hasAbstract);
        this.pmcRefCount = new Integer(pms.pmcRefCount);
        this.fullJournalName = pms.fullJournalName;
        this.eLocationId = pms.eLocationId;
        this.so = pms.so;
        this.authorList = new ArrayList<String>();
        for (String author : pms.authorList) {
            this.authorList.add(author);
        }
        this.langList = new ArrayList<String>();
        for (String lang : pms.langList) {
            this.langList.add(lang);
        }
        this.pubTypeList = new ArrayList<String>();
        for (String pubType : pms.pubTypeList) {
            this.pubTypeList.add(pubType);
        }
        this.articleIds = new HashMap<String,String>();
        for (String key : pms.articleIds.keySet()) {
            String val = pms.articleIds.get(key);
            this.articleIds.put(key, val);
        }
        this.history = new HashMap<String,String>();
        for (String key : pms.history.keySet()) {
            String val = pms.history.get(key);
            this.history.put(key, val);
        }
        this.references = new HashMap<String,String>();
        for (String key : pms.references.keySet()) {
            String val = pms.references.get(key);
            this.references.put(key, val);
        }
    }

    /**
     * Spit out a string representation of the summary.
     */
    public String toString() {
        String out = "";
        out += "ID:"+id+"\n";
        out += "PubDate:"+pubDate+"\n";
        out += "EPubDate:"+ePubDate+"\n";
        out += "Source:"+source+"\n";
        for (String author : authorList) {
            out += "Author:"+author+"\n";
        }
        out += "LastAuthor:"+lastAuthor+"\n";
        out += "Title:"+title+"\n";
        out += "Volume:"+volume+"\n";
        out += "Issue:"+issue+"\n";
        out += "Pages:"+pages+"\n";
        for (String lang : langList) {
            out += "Lang:"+lang+"\n";
        }
        out += "NlmUniqueID:"+nlmUniqueId+"\n";
        out += "ISSN:"+issn+"\n";
        out += "ESSN:"+essn+"\n";
        for (String pubType : pubTypeList) {
            out += "PubType:"+pubType+"\n";
        }
        out += "RecordStatus:"+recordStatus+"\n";
        out += "PubStatus:"+pubStatus+"\n";
        out += "DOI:"+doi+"\n";
        out += "HasAbstract:"+hasAbstract+"\n";
        out += "PmcRefCount:"+pmcRefCount+"\n";
        out += "FullJournalName:"+fullJournalName+"\n";
        out += "ELocationID:"+eLocationId+"\n";
        out += "SO:"+so+"\n";
        return out;
    }

}
