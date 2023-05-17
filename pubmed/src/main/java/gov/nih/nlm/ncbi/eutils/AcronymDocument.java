/*
 * An XML document type.
 * Localname: Acronym
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AcronymDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Acronym(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface AcronymDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AcronymDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "acronyme2acdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Acronym" element
     */
    java.lang.String getAcronym();

    /**
     * Gets (as xml) the "Acronym" element
     */
    org.apache.xmlbeans.XmlString xgetAcronym();

    /**
     * Sets the "Acronym" element
     */
    void setAcronym(java.lang.String acronym);

    /**
     * Sets (as xml) the "Acronym" element
     */
    void xsetAcronym(org.apache.xmlbeans.XmlString acronym);
}
