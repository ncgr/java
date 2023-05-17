/*
 * An XML document type.
 * Localname: Language
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.LanguageDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Language(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface LanguageDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.LanguageDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "language8165doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Language" element
     */
    java.lang.String getLanguage();

    /**
     * Gets (as xml) the "Language" element
     */
    org.apache.xmlbeans.XmlString xgetLanguage();

    /**
     * Sets the "Language" element
     */
    void setLanguage(java.lang.String language);

    /**
     * Sets (as xml) the "Language" element
     */
    void xsetLanguage(org.apache.xmlbeans.XmlString language);
}
