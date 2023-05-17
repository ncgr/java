/*
 * XML Type:  AccessionNumberListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AccessionNumberListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML AccessionNumberListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface AccessionNumberListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AccessionNumberListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "accessionnumberlisttype8c7atype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "AccessionNumber" elements
     */
    java.util.List<java.lang.String> getAccessionNumberList();

    /**
     * Gets array of all "AccessionNumber" elements
     */
    java.lang.String[] getAccessionNumberArray();

    /**
     * Gets ith "AccessionNumber" element
     */
    java.lang.String getAccessionNumberArray(int i);

    /**
     * Gets (as xml) a List of "AccessionNumber" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetAccessionNumberList();

    /**
     * Gets (as xml) array of all "AccessionNumber" elements
     */
    org.apache.xmlbeans.XmlString[] xgetAccessionNumberArray();

    /**
     * Gets (as xml) ith "AccessionNumber" element
     */
    org.apache.xmlbeans.XmlString xgetAccessionNumberArray(int i);

    /**
     * Returns number of "AccessionNumber" element
     */
    int sizeOfAccessionNumberArray();

    /**
     * Sets array of all "AccessionNumber" element
     */
    void setAccessionNumberArray(java.lang.String[] accessionNumberArray);

    /**
     * Sets ith "AccessionNumber" element
     */
    void setAccessionNumberArray(int i, java.lang.String accessionNumber);

    /**
     * Sets (as xml) array of all "AccessionNumber" element
     */
    void xsetAccessionNumberArray(org.apache.xmlbeans.XmlString[] accessionNumberArray);

    /**
     * Sets (as xml) ith "AccessionNumber" element
     */
    void xsetAccessionNumberArray(int i, org.apache.xmlbeans.XmlString accessionNumber);

    /**
     * Inserts the value as the ith "AccessionNumber" element
     */
    void insertAccessionNumber(int i, java.lang.String accessionNumber);

    /**
     * Appends the value as the last "AccessionNumber" element
     */
    void addAccessionNumber(java.lang.String accessionNumber);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "AccessionNumber" element
     */
    org.apache.xmlbeans.XmlString insertNewAccessionNumber(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "AccessionNumber" element
     */
    org.apache.xmlbeans.XmlString addNewAccessionNumber();

    /**
     * Removes the ith "AccessionNumber" element
     */
    void removeAccessionNumber(int i);
}
