/*
 * An XML document type.
 * Localname: AccessionNumber
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AccessionNumberDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one AccessionNumber(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface AccessionNumberDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AccessionNumberDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "accessionnumber32a2doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "AccessionNumber" element
     */
    java.lang.String getAccessionNumber();

    /**
     * Gets (as xml) the "AccessionNumber" element
     */
    org.apache.xmlbeans.XmlString xgetAccessionNumber();

    /**
     * Sets the "AccessionNumber" element
     */
    void setAccessionNumber(java.lang.String accessionNumber);

    /**
     * Sets (as xml) the "AccessionNumber" element
     */
    void xsetAccessionNumber(org.apache.xmlbeans.XmlString accessionNumber);
}
