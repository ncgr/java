/*
 * An XML document type.
 * Localname: CopyrightInformation
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CopyrightInformationDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CopyrightInformation(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface CopyrightInformationDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.CopyrightInformationDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "copyrightinformation7838doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CopyrightInformation" element
     */
    java.lang.String getCopyrightInformation();

    /**
     * Gets (as xml) the "CopyrightInformation" element
     */
    org.apache.xmlbeans.XmlString xgetCopyrightInformation();

    /**
     * Sets the "CopyrightInformation" element
     */
    void setCopyrightInformation(java.lang.String copyrightInformation);

    /**
     * Sets (as xml) the "CopyrightInformation" element
     */
    void xsetCopyrightInformation(org.apache.xmlbeans.XmlString copyrightInformation);
}
