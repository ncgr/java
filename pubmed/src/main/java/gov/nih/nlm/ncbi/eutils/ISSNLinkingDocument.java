/*
 * An XML document type.
 * Localname: ISSNLinking
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ISSNLinkingDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ISSNLinking(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface ISSNLinkingDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ISSNLinkingDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "issnlinking2aecdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ISSNLinking" element
     */
    java.lang.String getISSNLinking();

    /**
     * Gets (as xml) the "ISSNLinking" element
     */
    org.apache.xmlbeans.XmlString xgetISSNLinking();

    /**
     * Sets the "ISSNLinking" element
     */
    void setISSNLinking(java.lang.String issnLinking);

    /**
     * Sets (as xml) the "ISSNLinking" element
     */
    void xsetISSNLinking(org.apache.xmlbeans.XmlString issnLinking);
}
