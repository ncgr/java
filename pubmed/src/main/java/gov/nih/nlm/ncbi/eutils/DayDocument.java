/*
 * An XML document type.
 * Localname: Day
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DayDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Day(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface DayDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.DayDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "dayeeb3doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Day" element
     */
    java.lang.String getDay();

    /**
     * Gets (as xml) the "Day" element
     */
    org.apache.xmlbeans.XmlString xgetDay();

    /**
     * Sets the "Day" element
     */
    void setDay(java.lang.String day);

    /**
     * Sets (as xml) the "Day" element
     */
    void xsetDay(org.apache.xmlbeans.XmlString day);
}
