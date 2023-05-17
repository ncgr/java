/*
 * An XML document type.
 * Localname: MedlineDate
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineDateDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one MedlineDate(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface MedlineDateDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MedlineDateDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlinedate6081doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "MedlineDate" element
     */
    java.lang.String getMedlineDate();

    /**
     * Gets (as xml) the "MedlineDate" element
     */
    org.apache.xmlbeans.XmlString xgetMedlineDate();

    /**
     * Sets the "MedlineDate" element
     */
    void setMedlineDate(java.lang.String medlineDate);

    /**
     * Sets (as xml) the "MedlineDate" element
     */
    void xsetMedlineDate(org.apache.xmlbeans.XmlString medlineDate);
}
