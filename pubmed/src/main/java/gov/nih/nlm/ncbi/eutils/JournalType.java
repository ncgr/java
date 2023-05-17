/*
 * XML Type:  JournalType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.JournalType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML JournalType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface JournalType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.JournalType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "journaltype4c0etype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ISSN" element
     */
    gov.nih.nlm.ncbi.eutils.ISSNType getISSN();

    /**
     * True if has "ISSN" element
     */
    boolean isSetISSN();

    /**
     * Sets the "ISSN" element
     */
    void setISSN(gov.nih.nlm.ncbi.eutils.ISSNType issn);

    /**
     * Appends and returns a new empty "ISSN" element
     */
    gov.nih.nlm.ncbi.eutils.ISSNType addNewISSN();

    /**
     * Unsets the "ISSN" element
     */
    void unsetISSN();

    /**
     * Gets the "JournalIssue" element
     */
    gov.nih.nlm.ncbi.eutils.JournalIssueType getJournalIssue();

    /**
     * Sets the "JournalIssue" element
     */
    void setJournalIssue(gov.nih.nlm.ncbi.eutils.JournalIssueType journalIssue);

    /**
     * Appends and returns a new empty "JournalIssue" element
     */
    gov.nih.nlm.ncbi.eutils.JournalIssueType addNewJournalIssue();

    /**
     * Gets the "Title" element
     */
    java.lang.String getTitle();

    /**
     * Gets (as xml) the "Title" element
     */
    org.apache.xmlbeans.XmlString xgetTitle();

    /**
     * True if has "Title" element
     */
    boolean isSetTitle();

    /**
     * Sets the "Title" element
     */
    void setTitle(java.lang.String title);

    /**
     * Sets (as xml) the "Title" element
     */
    void xsetTitle(org.apache.xmlbeans.XmlString title);

    /**
     * Unsets the "Title" element
     */
    void unsetTitle();

    /**
     * Gets the "ISOAbbreviation" element
     */
    java.lang.String getISOAbbreviation();

    /**
     * Gets (as xml) the "ISOAbbreviation" element
     */
    org.apache.xmlbeans.XmlString xgetISOAbbreviation();

    /**
     * True if has "ISOAbbreviation" element
     */
    boolean isSetISOAbbreviation();

    /**
     * Sets the "ISOAbbreviation" element
     */
    void setISOAbbreviation(java.lang.String isoAbbreviation);

    /**
     * Sets (as xml) the "ISOAbbreviation" element
     */
    void xsetISOAbbreviation(org.apache.xmlbeans.XmlString isoAbbreviation);

    /**
     * Unsets the "ISOAbbreviation" element
     */
    void unsetISOAbbreviation();
}
