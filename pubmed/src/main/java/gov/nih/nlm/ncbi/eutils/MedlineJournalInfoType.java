/*
 * XML Type:  MedlineJournalInfoType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML MedlineJournalInfoType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface MedlineJournalInfoType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlinejournalinfotype11aetype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Country" element
     */
    java.lang.String getCountry();

    /**
     * Gets (as xml) the "Country" element
     */
    org.apache.xmlbeans.XmlString xgetCountry();

    /**
     * True if has "Country" element
     */
    boolean isSetCountry();

    /**
     * Sets the "Country" element
     */
    void setCountry(java.lang.String country);

    /**
     * Sets (as xml) the "Country" element
     */
    void xsetCountry(org.apache.xmlbeans.XmlString country);

    /**
     * Unsets the "Country" element
     */
    void unsetCountry();

    /**
     * Gets the "MedlineTA" element
     */
    java.lang.String getMedlineTA();

    /**
     * Gets (as xml) the "MedlineTA" element
     */
    org.apache.xmlbeans.XmlString xgetMedlineTA();

    /**
     * Sets the "MedlineTA" element
     */
    void setMedlineTA(java.lang.String medlineTA);

    /**
     * Sets (as xml) the "MedlineTA" element
     */
    void xsetMedlineTA(org.apache.xmlbeans.XmlString medlineTA);

    /**
     * Gets the "NlmUniqueID" element
     */
    java.lang.String getNlmUniqueID();

    /**
     * Gets (as xml) the "NlmUniqueID" element
     */
    org.apache.xmlbeans.XmlString xgetNlmUniqueID();

    /**
     * True if has "NlmUniqueID" element
     */
    boolean isSetNlmUniqueID();

    /**
     * Sets the "NlmUniqueID" element
     */
    void setNlmUniqueID(java.lang.String nlmUniqueID);

    /**
     * Sets (as xml) the "NlmUniqueID" element
     */
    void xsetNlmUniqueID(org.apache.xmlbeans.XmlString nlmUniqueID);

    /**
     * Unsets the "NlmUniqueID" element
     */
    void unsetNlmUniqueID();

    /**
     * Gets the "ISSNLinking" element
     */
    java.lang.String getISSNLinking();

    /**
     * Gets (as xml) the "ISSNLinking" element
     */
    org.apache.xmlbeans.XmlString xgetISSNLinking();

    /**
     * True if has "ISSNLinking" element
     */
    boolean isSetISSNLinking();

    /**
     * Sets the "ISSNLinking" element
     */
    void setISSNLinking(java.lang.String issnLinking);

    /**
     * Sets (as xml) the "ISSNLinking" element
     */
    void xsetISSNLinking(org.apache.xmlbeans.XmlString issnLinking);

    /**
     * Unsets the "ISSNLinking" element
     */
    void unsetISSNLinking();
}
