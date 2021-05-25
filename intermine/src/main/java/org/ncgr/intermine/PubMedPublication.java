package org.ncgr.intermine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.intermine.dataconversion.DataConverter;
import org.intermine.xml.full.Item;

import org.ncgr.pubmed.PubMedSummary;

import org.xml.sax.SAXException;


/**
 * Encapulates a PubMed publication with a Publication Item and an associated Authors List for upstream storage.
 *
 * NOTE: the authors list is NOT added to the publication collection here. Do that upstream.
 */
public class PubMedPublication {

    PubMedSummary summary;
    Item publication;
    List<Item> authors;

    /**
     * Construct from a DataConverter and PubMed ID.
     */
    public PubMedPublication(DataConverter converter, int pmid) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // get the summary
        summary = new PubMedSummary();
        summary.search(pmid);
        if (summary.id!=0) {
            // create and populate the Publication Item
            publication = converter.createItem("Publication");
            populateFromSummary(converter);
        }
    }

    /**
     * Construct from a DataConverter and a PubMedSummary.
     */
    public PubMedPublication(DataConverter converter, PubMedSummary summary) {
        this.summary = summary;
        if (summary.id!=0) {
            // create and populate the Publication Item
            publication = converter.createItem("Publication");
            populateFromSummary(converter);
        }
    }        

    /**
     * Populate instance fields from the summary
     */
    void populateFromSummary(DataConverter converter) {
        // mandatory fields
        publication.setAttribute("title", summary.title);
	publication.setAttribute("pubMedId", String.valueOf(summary.id));
        publication.setAttribute("year", String.valueOf(getYear(summary.pubDate)));
        publication.setAttribute("journal", summary.source);
        // optional fields
        if (getMonth(summary.pubDate)!=null) publication.setAttribute("month", getMonth(summary.pubDate));
        if (summary.pages!=null && summary.pages.length()>0) publication.setAttribute("pages", summary.pages);
        if (summary.issue!=null && summary.issue.length()>0) publication.setAttribute("issue", summary.issue);
        if (summary.lastAuthor!=null && summary.lastAuthor.length()>0) publication.setAttribute("lastAuthor", summary.lastAuthor);
        if (summary.doi!=null && summary.doi.length()>0) publication.setAttribute("doi", summary.doi);
        if (summary.volume!=null && summary.volume.length()>0) publication.setAttribute("volume", summary.volume);
        if (summary.authorList!=null && summary.authorList.size()>0) publication.setAttribute("firstAuthor", summary.authorList.get(0));
        // the list of Author Items
        authors = new LinkedList<Item>();
        for (String authorName : summary.authorList) {
            Item author = converter.createItem("Author");
            author.setAttribute("name", authorName);
            String[] parts = authorName.split(" ");
            author.setAttribute("lastName", parts[0]);
            author.setAttribute("initials", parts[1]);
            authors.add(author);
        }
    }

    /**
     * publication getter
     */
    public Item getPublication() {
        return publication;
    }

    /**
     * authors getter
     */
    public List<Item> getAuthors() {
        return authors;
    }

    /**
     * summary getter
     */
    public PubMedSummary getSummary() {
        return summary;
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
        if (parts.length>1) {
            return parts[1];
        } else {
            return null;
        }
    }

}
