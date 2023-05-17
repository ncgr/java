/*
 * An XML document type.
 * Localname: RegistryNumber
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.RegistryNumberDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one RegistryNumber(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface RegistryNumberDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.RegistryNumberDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "registrynumber52d7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "RegistryNumber" element
     */
    java.lang.String getRegistryNumber();

    /**
     * Gets (as xml) the "RegistryNumber" element
     */
    org.apache.xmlbeans.XmlString xgetRegistryNumber();

    /**
     * Sets the "RegistryNumber" element
     */
    void setRegistryNumber(java.lang.String registryNumber);

    /**
     * Sets (as xml) the "RegistryNumber" element
     */
    void xsetRegistryNumber(org.apache.xmlbeans.XmlString registryNumber);
}
