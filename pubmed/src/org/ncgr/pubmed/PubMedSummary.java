package org.ncgr.pubmed;

/*
 * Copyright (C) 2015-2016 NCGR
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

/**
 * A reader that parses a PubMed eSummaryResult XML response.
 *
 * Results are stored in public instance variables rather than getters.
 *
 * @author Sam Hokin
 */
public class PubMedSummary {
    
    public static String CHARSET = "UTF-8";

    // eSummaryResult/DocSum fields
    public int id;
    public String pubDate;
    public String ePubDate;
    public String source;
    public Set<String> authorList = new LinkedHashSet<String>();
    public String lastAuthor;
    public String title;
    public String volume;
    public String issue;
    public String pages;
    public Set<String> langList = new LinkedHashSet<String>();
    public String nlmUniqueId;
    public String issn;
    public String essn;
    public Set<String> pubTypeList = new LinkedHashSet<String>();
    public String recordStatus;
    public String pubStatus;
    public Map<String,String> articleIds = new HashMap<String,String>();
    public String doi;
    public Map<String,String> history = new HashMap<String,String>();
    public Map<String,String> references = new HashMap<String,String>();
    public boolean hasAbstract;
    public int pmcRefCount;
    public String fullJournalName;
    public String eLocationId;
    public String so;

    /**
     * Get a summary of a PMID
     */
    public PubMedSummary(int id) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {

        // "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=25283805"

        // form URL
        String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+id;

        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);
        doc.getDocumentElement().normalize(); // recommended

        // if article doesn't exist, response has ERROR tag
        boolean exists = doc.getElementsByTagName("ERROR").item(0)==null;

        // parse-o-rama
        if (exists) parse(doc);
        
    }

    /**
     * Get a summary of the first match to title (if exists).
     */
    public PubMedSummary(String title) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {

        // "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=An RNA-Seq based gene expression atlas of the common bean.[Title]"

        // form search URL
        String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(title,"UTF-8")+"[Title]";

        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);
        doc.getDocumentElement().normalize(); // recommended

        // if article exists, count>0
        Node countNode = doc.getElementsByTagName("Count").item(0);
        int count = Integer.parseInt(countNode.getTextContent());
        
        int id = 0;
        if (count>0) {
            Node idNode = doc.getElementsByTagName("Id").item(0);
            id = Integer.parseInt(idNode.getTextContent());
        }

        if (id>0) {
            // form summary URL
            url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+id;

            // parse the URL response
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(url);
            doc.getDocumentElement().normalize(); // recommended

            // if article doesn't exist, response has ERROR tag
            boolean exists = doc.getElementsByTagName("ERROR").item(0)==null;

            // parse-o-rama
            if (exists) parse(doc);
        }

    }


    /**
     * Parse a document into instance variables.
     */
    void parse(Document doc) {

        this.id = Integer.parseInt(doc.getElementsByTagName("Id").item(0).getTextContent());

        NodeList items = doc.getElementsByTagName("Item");
        for (int i=0; i<items.getLength(); i++) {
            Node item = items.item(i);
            Node itemName = item.getAttributes().getNamedItem("Name");
            String itemValue = item.getTextContent();
            boolean inList = false;
            // process singletons
            switch (itemName.getTextContent()) {
            case "PubDate": pubDate = itemValue; break;
            case "EPubDate": ePubDate = itemValue; break;
            case "Source": source = itemValue; break;
            case "AuthorList":
                inList = true;
                while (inList) {
                    Node listItem = items.item(++i);
                    Node listItemName = listItem.getAttributes().getNamedItem("Name");
                    String listItemValue = listItem.getTextContent();
                    if (listItemName.getTextContent().equals("Author")) {
                        authorList.add(listItemValue);
                    } else {
                        i--;
                        inList = false;
                    }
                }
                break;
            case "LastAuthor": lastAuthor = itemValue; break;
            case "Title": title = itemValue; break;
            case "Volume": volume = itemValue; break;
            case "Issue": issue = itemValue; break;
            case "Pages": pages = itemValue; break;
            case "LangList":
                inList = true;
                while (inList) {
                    Node listItem = items.item(++i);
                    Node listItemName = listItem.getAttributes().getNamedItem("Name");
                    String listItemValue = listItem.getTextContent();
                    if (listItemName.getTextContent().equals("Lang")) {
                        langList.add(listItemValue);
                    } else {
                        i--;
                        inList = false;
                    }
                }
                break;
            case "NlmUniqueID": nlmUniqueId = itemValue; break;
            case "ISSN": issn = itemValue; break;
            case "ESSN": essn = itemValue; break;
            case "PubTypeList":
                inList = true;
                while (inList) {
                    Node listItem = items.item(++i);
                    Node listItemName = listItem.getAttributes().getNamedItem("Name");
                    String listItemValue = listItem.getTextContent();
                    if (listItemName.getTextContent().equals("PubType")) {
                        pubTypeList.add(listItemValue);
                    } else {
                        i--;
                        inList = false;
                    }
                }
                break;
            case "RecordStatus": recordStatus = itemValue; break;
            case "PubStatus": pubStatus = itemValue; break;
            case "ArticleIds": break; // bail on this one for now
            case "DOI": doi = itemValue; break;
            case "History": break;    // bail on this one for now
            case "References": break; // bail on this one for now
            case "HasAbstract": hasAbstract = (itemValue.equals("1")); break;
            case "PmcRefCount": pmcRefCount = Integer.parseInt(itemValue); break;
            case "FullJournalName": fullJournalName = itemValue; break;
            case "ELocationID": eLocationId = itemValue; break;
            case "SO": so = itemValue; break;
            default: break;
            }
        }

    }

    /**
     * Command-line query
     */
    public static void main(String[] args) {
        
        if (args.length!=1) {
            System.err.println("Usage: PubMedSummary PMID|Title");
            System.exit(1);
        }

        try {

            PubMedSummary pms;
            try {
                int id = Integer.parseInt(args[0]);
                pms = new PubMedSummary(id);
            } catch (Exception e) {
                String title = args[0];
                pms = new PubMedSummary(title);
            }
            
            if (pms.id==0) {
                System.err.println("PMID or title "+args[0]+" not found. Exiting.");
                System.exit(1);
            }

            System.out.println("ID:"+pms.id);
            System.out.println("PubDate:"+pms.pubDate);
            System.out.println("EPubDate:"+pms.ePubDate);
            System.out.println("Source:"+pms.source);
            for (String author : pms.authorList) {
                System.out.println("Author:"+author);
            }
            System.out.println("LastAuthor:"+pms.lastAuthor);
            System.out.println("Title:"+pms.title);
            System.out.println("Volume:"+pms.volume);
            System.out.println("Issue:"+pms.issue);
            System.out.println("Pages:"+pms.pages);
            for (String lang : pms.langList) {
                System.out.println("Lang:"+lang);
            }
            System.out.println("NlmUniqueID:"+pms.nlmUniqueId);
            System.out.println("ISSN:"+pms.issn);
            System.out.println("ESSN:"+pms.essn);
            for (String pubType : pms.pubTypeList) {
                System.out.println("PubType:"+pubType);
            }
            System.out.println("RecordStatus:"+pms.recordStatus);
            System.out.println("PubStatus:"+pms.pubStatus);
            System.out.println("DOI:"+pms.doi);
            System.out.println("HasAbstract:"+pms.hasAbstract);
            System.out.println("PmcRefCount:"+pms.pmcRefCount);
            System.out.println("FullJournalName:"+pms.fullJournalName);
            System.out.println("ELocationID:"+pms.eLocationId);
            System.out.println("SO:"+pms.so);
                
        } catch (Exception ex) {
            System.err.println(ex.toString());
            System.exit(1);
        }
        
    }

}

