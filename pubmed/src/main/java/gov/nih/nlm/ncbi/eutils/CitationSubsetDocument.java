/*
 * An XML document type.
 * Localname: CitationSubset
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CitationSubsetDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CitationSubset(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface CitationSubsetDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.CitationSubsetDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "citationsubset0df4doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CitationSubset" element
     */
    java.lang.String getCitationSubset();

    /**
     * Gets (as xml) the "CitationSubset" element
     */
    org.apache.xmlbeans.XmlString xgetCitationSubset();

    /**
     * Sets the "CitationSubset" element
     */
    void setCitationSubset(java.lang.String citationSubset);

    /**
     * Sets (as xml) the "CitationSubset" element
     */
    void xsetCitationSubset(org.apache.xmlbeans.XmlString citationSubset);
}
