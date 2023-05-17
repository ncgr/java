/*
 * An XML document type.
 * Localname: MedlinePgn
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlinePgnDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one MedlinePgn(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface MedlinePgnDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MedlinePgnDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlinepgn4126doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "MedlinePgn" element
     */
    java.lang.String getMedlinePgn();

    /**
     * Gets (as xml) the "MedlinePgn" element
     */
    org.apache.xmlbeans.XmlString xgetMedlinePgn();

    /**
     * Sets the "MedlinePgn" element
     */
    void setMedlinePgn(java.lang.String medlinePgn);

    /**
     * Sets (as xml) the "MedlinePgn" element
     */
    void xsetMedlinePgn(org.apache.xmlbeans.XmlString medlinePgn);
}
