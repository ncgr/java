/*
 * XML Type:  DeleteCitationType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DeleteCitationType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML DeleteCitationType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface DeleteCitationType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.DeleteCitationType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "deletecitationtypecb81type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "PMID" elements
     */
    java.util.List<java.lang.String> getPMIDList();

    /**
     * Gets array of all "PMID" elements
     */
    java.lang.String[] getPMIDArray();

    /**
     * Gets ith "PMID" element
     */
    java.lang.String getPMIDArray(int i);

    /**
     * Gets (as xml) a List of "PMID" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetPMIDList();

    /**
     * Gets (as xml) array of all "PMID" elements
     */
    org.apache.xmlbeans.XmlString[] xgetPMIDArray();

    /**
     * Gets (as xml) ith "PMID" element
     */
    org.apache.xmlbeans.XmlString xgetPMIDArray(int i);

    /**
     * Returns number of "PMID" element
     */
    int sizeOfPMIDArray();

    /**
     * Sets array of all "PMID" element
     */
    void setPMIDArray(java.lang.String[] pmidArray);

    /**
     * Sets ith "PMID" element
     */
    void setPMIDArray(int i, java.lang.String pmid);

    /**
     * Sets (as xml) array of all "PMID" element
     */
    void xsetPMIDArray(org.apache.xmlbeans.XmlString[] pmidArray);

    /**
     * Sets (as xml) ith "PMID" element
     */
    void xsetPMIDArray(int i, org.apache.xmlbeans.XmlString pmid);

    /**
     * Inserts the value as the ith "PMID" element
     */
    void insertPMID(int i, java.lang.String pmid);

    /**
     * Appends the value as the last "PMID" element
     */
    void addPMID(java.lang.String pmid);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PMID" element
     */
    org.apache.xmlbeans.XmlString insertNewPMID(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "PMID" element
     */
    org.apache.xmlbeans.XmlString addNewPMID();

    /**
     * Removes the ith "PMID" element
     */
    void removePMID(int i);
}
