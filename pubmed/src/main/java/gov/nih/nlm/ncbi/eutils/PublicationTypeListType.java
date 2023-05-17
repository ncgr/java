/*
 * XML Type:  PublicationTypeListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PublicationTypeListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PublicationTypeListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PublicationTypeListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PublicationTypeListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "publicationtypelisttype6fe1type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "PublicationType" elements
     */
    java.util.List<java.lang.String> getPublicationTypeList();

    /**
     * Gets array of all "PublicationType" elements
     */
    java.lang.String[] getPublicationTypeArray();

    /**
     * Gets ith "PublicationType" element
     */
    java.lang.String getPublicationTypeArray(int i);

    /**
     * Gets (as xml) a List of "PublicationType" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetPublicationTypeList();

    /**
     * Gets (as xml) array of all "PublicationType" elements
     */
    org.apache.xmlbeans.XmlString[] xgetPublicationTypeArray();

    /**
     * Gets (as xml) ith "PublicationType" element
     */
    org.apache.xmlbeans.XmlString xgetPublicationTypeArray(int i);

    /**
     * Returns number of "PublicationType" element
     */
    int sizeOfPublicationTypeArray();

    /**
     * Sets array of all "PublicationType" element
     */
    void setPublicationTypeArray(java.lang.String[] publicationTypeArray);

    /**
     * Sets ith "PublicationType" element
     */
    void setPublicationTypeArray(int i, java.lang.String publicationType);

    /**
     * Sets (as xml) array of all "PublicationType" element
     */
    void xsetPublicationTypeArray(org.apache.xmlbeans.XmlString[] publicationTypeArray);

    /**
     * Sets (as xml) ith "PublicationType" element
     */
    void xsetPublicationTypeArray(int i, org.apache.xmlbeans.XmlString publicationType);

    /**
     * Inserts the value as the ith "PublicationType" element
     */
    void insertPublicationType(int i, java.lang.String publicationType);

    /**
     * Appends the value as the last "PublicationType" element
     */
    void addPublicationType(java.lang.String publicationType);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PublicationType" element
     */
    org.apache.xmlbeans.XmlString insertNewPublicationType(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "PublicationType" element
     */
    org.apache.xmlbeans.XmlString addNewPublicationType();

    /**
     * Removes the ith "PublicationType" element
     */
    void removePublicationType(int i);
}
