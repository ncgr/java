package org.ncgr.intermine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.intermine.dataconversion.DataConverter;
import org.intermine.xml.full.Item;

import org.ncgr.pubmed.PubMedSummary;

import org.xml.sax.SAXException;


/**
 * Static methods for dealing with publications.
 *
 *
 * PubMedSummary fields:
 * int id;
 * String pubDate;
 * String ePubDate;
 * String source;
 * String lastAuthor;
 * String title;
 * String volume;
 * String issue;
 * String pages;
 * String nlmUniqueId;
 * String issn;
 * String essn;
 * String recordStatus;
 * String pubStatus;
 * String doi;
 * boolean hasAbstract;
 * int pmcRefCount;
 * String fullJournalName;
 * String eLocationId;
 * String so;
 * Set<String> authorList;
 * Set<String> langList;
 * Set<String> pubTypeList;
 * Map<String,String> articleIds;
 * Map<String,String> history;
 * Map<String,String> references;
 *
 * <attribute name="year" type="java.lang.Integer"/>
 * <attribute name="issue" type="java.lang.String"/>
 * <attribute name="lastAuthor" type="java.lang.String"/>
 * <attribute name="title" type="java.lang.String"/>
 * <attribute name="pages" type="java.lang.String"/>
 * <attribute name="doi" type="java.lang.String"/>
 * <attribute name="volume" type="java.lang.String"/>
 * <attribute name="journal" type="java.lang.String"/>
 * <attribute name="firstAuthor" type="java.lang.String"/>
 * <attribute name="month" type="java.lang.String"/>
 * <attribute name="abstractText" type="java.lang.String"/>
 * <attribute name="pubMedId" type="java.lang.String"/>
 * <collection name="authors" referenced-type="Author" reverse-reference="publications"/>
 * <collection name="bioEntities" referenced-type="BioEntity" reverse-reference="publications"/>
 * <collection name="crossReferences" referenced-type="DatabaseReference" reverse-reference="subject"/>
 * <collection name="meshTerms" referenced-type="MeshTerm" reverse-reference="publications"/>
 *
 * <attribute name="firstName" type="java.lang.String"/>
 * <attribute name="initials" type="java.lang.String"/>
 * <attribute name="lastName" type="java.lang.String"/>
 * <attribute name="name" type="java.lang.String"/>
 * <collection name="publications" referenced-type="Publication" reverse-reference="authors"/>
 *
 */
public class PublicationTools {

    public static Item getPublicationFromPMID(DataConverter converter, int pmid) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        PubMedSummary summary = new PubMedSummary();
        summary.search(pmid);
        // the publication Item
        Item publication = converter.createItem("Publication");
        publication.setAttribute("year", String.valueOf(getYear(summary.pubDate)));
        publication.setAttribute("issue", String.valueOf(summary.issue));
        publication.setAttribute("lastAuthor", summary.lastAuthor);
        publication.setAttribute("title", summary.title);
        publication.setAttribute("pages", summary.pages);
        publication.setAttribute("doi", summary.doi);
        publication.setAttribute("volume", summary.volume);
        publication.setAttribute("journal", summary.fullJournalName);
        Object[] authors = summary.authorList.toArray();
        publication.setAttribute("firstAuthor", (String)authors[0]);
        publication.setAttribute("month", getMonth(summary.pubDate));
        publication.setAttribute("pubMedId", String.valueOf(summary.id));
        // the authors collection
        for (String authorName : summary.authorList) {
            Item author = converter.createItem("Author");
            author.setAttribute("name", authorName);
            String[] parts = authorName.split(" ");
            author.setAttribute("lastName", parts[0]);
            author.setAttribute("initials", parts[1]);
            publication.addToCollection("authors", author);
        }
        return publication;
    }

    /**
     * Parse the year from a string like "2017 Mar" or "2017 Feb 3"
     */
    static int getYear(String dateString) {
        String[] parts = dateString.split(" ");
        return Integer.parseInt(parts[0]);
    }

    /**
     * Parse the month from a string like "2017 Mar" or "2017 Feb 3"
     */
    static String getMonth(String dateString) {
        String[] parts = dateString.split(" ");
        if (parts.length>=2) {
            return parts[1];
        } else {
            return null;
        }
    }

}
