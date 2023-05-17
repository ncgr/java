/*
 * An XML document type.
 * Localname: NlmUniqueID
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.NlmUniqueIDDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one NlmUniqueID(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface NlmUniqueIDDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.NlmUniqueIDDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "nlmuniqueid78b4doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "NlmUniqueID" element
     */
    java.lang.String getNlmUniqueID();

    /**
     * Gets (as xml) the "NlmUniqueID" element
     */
    org.apache.xmlbeans.XmlString xgetNlmUniqueID();

    /**
     * Sets the "NlmUniqueID" element
     */
    void setNlmUniqueID(java.lang.String nlmUniqueID);

    /**
     * Sets (as xml) the "NlmUniqueID" element
     */
    void xsetNlmUniqueID(org.apache.xmlbeans.XmlString nlmUniqueID);
}
