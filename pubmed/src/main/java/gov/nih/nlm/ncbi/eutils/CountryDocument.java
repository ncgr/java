/*
 * An XML document type.
 * Localname: Country
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CountryDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Country(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface CountryDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.CountryDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "countryb3f9doctype");
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
     * Sets the "Country" element
     */
    void setCountry(java.lang.String country);

    /**
     * Sets (as xml) the "Country" element
     */
    void xsetCountry(org.apache.xmlbeans.XmlString country);
}
