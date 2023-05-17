/*
 * An XML document type.
 * Localname: MedlineTA
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineTADocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one MedlineTA(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface MedlineTADocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MedlineTADocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlineta6fc2doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "MedlineTA" element
     */
    java.lang.String getMedlineTA();

    /**
     * Gets (as xml) the "MedlineTA" element
     */
    org.apache.xmlbeans.XmlString xgetMedlineTA();

    /**
     * Sets the "MedlineTA" element
     */
    void setMedlineTA(java.lang.String medlineTA);

    /**
     * Sets (as xml) the "MedlineTA" element
     */
    void xsetMedlineTA(org.apache.xmlbeans.XmlString medlineTA);
}
