/*
 * An XML document type.
 * Localname: LastName
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.LastNameDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one LastName(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface LastNameDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.LastNameDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "lastnamedddcdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "LastName" element
     */
    java.lang.String getLastName();

    /**
     * Gets (as xml) the "LastName" element
     */
    org.apache.xmlbeans.XmlString xgetLastName();

    /**
     * Sets the "LastName" element
     */
    void setLastName(java.lang.String lastName);

    /**
     * Sets (as xml) the "LastName" element
     */
    void xsetLastName(org.apache.xmlbeans.XmlString lastName);
}
