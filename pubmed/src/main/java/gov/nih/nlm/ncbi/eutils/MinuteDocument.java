/*
 * An XML document type.
 * Localname: Minute
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MinuteDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Minute(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface MinuteDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MinuteDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "minute5ae9doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Minute" element
     */
    java.lang.String getMinute();

    /**
     * Gets (as xml) the "Minute" element
     */
    org.apache.xmlbeans.XmlString xgetMinute();

    /**
     * Sets the "Minute" element
     */
    void setMinute(java.lang.String minute);

    /**
     * Sets (as xml) the "Minute" element
     */
    void xsetMinute(org.apache.xmlbeans.XmlString minute);
}
