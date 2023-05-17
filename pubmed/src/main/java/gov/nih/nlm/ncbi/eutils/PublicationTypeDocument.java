/*
 * An XML document type.
 * Localname: PublicationType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PublicationTypeDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one PublicationType(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface PublicationTypeDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PublicationTypeDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "publicationtype6d09doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "PublicationType" element
     */
    java.lang.String getPublicationType();

    /**
     * Gets (as xml) the "PublicationType" element
     */
    org.apache.xmlbeans.XmlString xgetPublicationType();

    /**
     * Sets the "PublicationType" element
     */
    void setPublicationType(java.lang.String publicationType);

    /**
     * Sets (as xml) the "PublicationType" element
     */
    void xsetPublicationType(org.apache.xmlbeans.XmlString publicationType);
}
