/*
 * XML Type:  HistoryType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.HistoryType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML HistoryType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface HistoryType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.HistoryType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "historytypef811type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "PubMedPubDate" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.PubMedPubDateType> getPubMedPubDateList();

    /**
     * Gets array of all "PubMedPubDate" elements
     */
    gov.nih.nlm.ncbi.eutils.PubMedPubDateType[] getPubMedPubDateArray();

    /**
     * Gets ith "PubMedPubDate" element
     */
    gov.nih.nlm.ncbi.eutils.PubMedPubDateType getPubMedPubDateArray(int i);

    /**
     * Returns number of "PubMedPubDate" element
     */
    int sizeOfPubMedPubDateArray();

    /**
     * Sets array of all "PubMedPubDate" element
     */
    void setPubMedPubDateArray(gov.nih.nlm.ncbi.eutils.PubMedPubDateType[] pubMedPubDateArray);

    /**
     * Sets ith "PubMedPubDate" element
     */
    void setPubMedPubDateArray(int i, gov.nih.nlm.ncbi.eutils.PubMedPubDateType pubMedPubDate);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PubMedPubDate" element
     */
    gov.nih.nlm.ncbi.eutils.PubMedPubDateType insertNewPubMedPubDate(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "PubMedPubDate" element
     */
    gov.nih.nlm.ncbi.eutils.PubMedPubDateType addNewPubMedPubDate();

    /**
     * Removes the ith "PubMedPubDate" element
     */
    void removePubMedPubDate(int i);
}
