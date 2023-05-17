/*
 * An XML document type.
 * Localname: EndPage
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.EndPageDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one EndPage(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface EndPageDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.EndPageDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "endpagedd05doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "EndPage" element
     */
    java.lang.String getEndPage();

    /**
     * Gets (as xml) the "EndPage" element
     */
    org.apache.xmlbeans.XmlString xgetEndPage();

    /**
     * Sets the "EndPage" element
     */
    void setEndPage(java.lang.String endPage);

    /**
     * Sets (as xml) the "EndPage" element
     */
    void xsetEndPage(org.apache.xmlbeans.XmlString endPage);
}
