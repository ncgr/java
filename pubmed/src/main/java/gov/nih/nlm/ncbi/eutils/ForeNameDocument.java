/*
 * An XML document type.
 * Localname: ForeName
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ForeNameDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ForeName(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface ForeNameDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ForeNameDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "forenamee4f6doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ForeName" element
     */
    java.lang.String getForeName();

    /**
     * Gets (as xml) the "ForeName" element
     */
    org.apache.xmlbeans.XmlString xgetForeName();

    /**
     * Sets the "ForeName" element
     */
    void setForeName(java.lang.String foreName);

    /**
     * Sets (as xml) the "ForeName" element
     */
    void xsetForeName(org.apache.xmlbeans.XmlString foreName);
}
