/*
 * An XML document type.
 * Localname: Second
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.SecondDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Second(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface SecondDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.SecondDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "second2089doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Second" element
     */
    java.lang.String getSecond();

    /**
     * Gets (as xml) the "Second" element
     */
    org.apache.xmlbeans.XmlString xgetSecond();

    /**
     * Sets the "Second" element
     */
    void setSecond(java.lang.String second);

    /**
     * Sets (as xml) the "Second" element
     */
    void xsetSecond(org.apache.xmlbeans.XmlString second);
}
