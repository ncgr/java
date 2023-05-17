/*
 * An XML document type.
 * Localname: Title
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.TitleDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Title(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface TitleDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.TitleDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "titlee437doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Title" element
     */
    java.lang.String getTitle();

    /**
     * Gets (as xml) the "Title" element
     */
    org.apache.xmlbeans.XmlString xgetTitle();

    /**
     * Sets the "Title" element
     */
    void setTitle(java.lang.String title);

    /**
     * Sets (as xml) the "Title" element
     */
    void xsetTitle(org.apache.xmlbeans.XmlString title);
}
