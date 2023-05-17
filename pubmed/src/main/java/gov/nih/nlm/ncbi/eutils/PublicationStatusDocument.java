/*
 * An XML document type.
 * Localname: PublicationStatus
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PublicationStatusDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one PublicationStatus(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface PublicationStatusDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PublicationStatusDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "publicationstatusc211doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "PublicationStatus" element
     */
    java.lang.String getPublicationStatus();

    /**
     * Gets (as xml) the "PublicationStatus" element
     */
    org.apache.xmlbeans.XmlString xgetPublicationStatus();

    /**
     * Sets the "PublicationStatus" element
     */
    void setPublicationStatus(java.lang.String publicationStatus);

    /**
     * Sets (as xml) the "PublicationStatus" element
     */
    void xsetPublicationStatus(org.apache.xmlbeans.XmlString publicationStatus);
}
