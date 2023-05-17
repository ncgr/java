/*
 * An XML document type.
 * Localname: GrantID
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GrantIDDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one GrantID(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface GrantIDDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.GrantIDDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "grantidacb8doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "GrantID" element
     */
    java.lang.String getGrantID();

    /**
     * Gets (as xml) the "GrantID" element
     */
    org.apache.xmlbeans.XmlString xgetGrantID();

    /**
     * Sets the "GrantID" element
     */
    void setGrantID(java.lang.String grantID);

    /**
     * Sets (as xml) the "GrantID" element
     */
    void xsetGrantID(org.apache.xmlbeans.XmlString grantID);
}
