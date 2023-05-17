/*
 * An XML document type.
 * Localname: Initials
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.InitialsDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Initials(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface InitialsDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.InitialsDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "initialsebaedoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Initials" element
     */
    java.lang.String getInitials();

    /**
     * Gets (as xml) the "Initials" element
     */
    org.apache.xmlbeans.XmlString xgetInitials();

    /**
     * Sets the "Initials" element
     */
    void setInitials(java.lang.String initials);

    /**
     * Sets (as xml) the "Initials" element
     */
    void xsetInitials(org.apache.xmlbeans.XmlString initials);
}
