/*
 * An XML document type.
 * Localname: RefSource
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.RefSourceDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one RefSource(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface RefSourceDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.RefSourceDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "refsource63c1doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "RefSource" element
     */
    java.lang.String getRefSource();

    /**
     * Gets (as xml) the "RefSource" element
     */
    org.apache.xmlbeans.XmlString xgetRefSource();

    /**
     * Sets the "RefSource" element
     */
    void setRefSource(java.lang.String refSource);

    /**
     * Sets (as xml) the "RefSource" element
     */
    void xsetRefSource(org.apache.xmlbeans.XmlString refSource);
}
