/*
 * XML Type:  GrantType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GrantType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML GrantType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface GrantType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.GrantType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "granttypeb0c9type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "GrantID" element
     */
    java.lang.String getGrantID();

    /**
     * Gets (as xml) the "GrantID" element
     */
    org.apache.xmlbeans.XmlString xgetGrantID();

    /**
     * True if has "GrantID" element
     */
    boolean isSetGrantID();

    /**
     * Sets the "GrantID" element
     */
    void setGrantID(java.lang.String grantID);

    /**
     * Sets (as xml) the "GrantID" element
     */
    void xsetGrantID(org.apache.xmlbeans.XmlString grantID);

    /**
     * Unsets the "GrantID" element
     */
    void unsetGrantID();

    /**
     * Gets the "Acronym" element
     */
    java.lang.String getAcronym();

    /**
     * Gets (as xml) the "Acronym" element
     */
    org.apache.xmlbeans.XmlString xgetAcronym();

    /**
     * True if has "Acronym" element
     */
    boolean isSetAcronym();

    /**
     * Sets the "Acronym" element
     */
    void setAcronym(java.lang.String acronym);

    /**
     * Sets (as xml) the "Acronym" element
     */
    void xsetAcronym(org.apache.xmlbeans.XmlString acronym);

    /**
     * Unsets the "Acronym" element
     */
    void unsetAcronym();

    /**
     * Gets the "Agency" element
     */
    java.lang.String getAgency();

    /**
     * Gets (as xml) the "Agency" element
     */
    org.apache.xmlbeans.XmlString xgetAgency();

    /**
     * Sets the "Agency" element
     */
    void setAgency(java.lang.String agency);

    /**
     * Sets (as xml) the "Agency" element
     */
    void xsetAgency(org.apache.xmlbeans.XmlString agency);

    /**
     * Gets the "Country" element
     */
    java.lang.String getCountry();

    /**
     * Gets (as xml) the "Country" element
     */
    org.apache.xmlbeans.XmlString xgetCountry();

    /**
     * Sets the "Country" element
     */
    void setCountry(java.lang.String country);

    /**
     * Sets (as xml) the "Country" element
     */
    void xsetCountry(org.apache.xmlbeans.XmlString country);
}
