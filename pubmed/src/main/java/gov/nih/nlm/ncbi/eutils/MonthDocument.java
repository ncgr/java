/*
 * An XML document type.
 * Localname: Month
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MonthDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Month(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface MonthDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MonthDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "monthb30fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Month" element
     */
    java.lang.String getMonth();

    /**
     * Gets (as xml) the "Month" element
     */
    org.apache.xmlbeans.XmlString xgetMonth();

    /**
     * Sets the "Month" element
     */
    void setMonth(java.lang.String month);

    /**
     * Sets (as xml) the "Month" element
     */
    void xsetMonth(org.apache.xmlbeans.XmlString month);
}
