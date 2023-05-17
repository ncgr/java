/*
 * An XML document type.
 * Localname: Hour
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.HourDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Hour(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface HourDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.HourDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "houraed9doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Hour" element
     */
    java.lang.String getHour();

    /**
     * Gets (as xml) the "Hour" element
     */
    org.apache.xmlbeans.XmlString xgetHour();

    /**
     * Sets the "Hour" element
     */
    void setHour(java.lang.String hour);

    /**
     * Sets (as xml) the "Hour" element
     */
    void xsetHour(org.apache.xmlbeans.XmlString hour);
}
