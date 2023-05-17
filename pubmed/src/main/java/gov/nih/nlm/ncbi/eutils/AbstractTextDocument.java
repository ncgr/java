/*
 * An XML document type.
 * Localname: AbstractText
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AbstractTextDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one AbstractText(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface AbstractTextDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AbstractTextDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "abstracttext016edoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "AbstractText" element
     */
    java.lang.String getAbstractText();

    /**
     * Gets (as xml) the "AbstractText" element
     */
    org.apache.xmlbeans.XmlString xgetAbstractText();

    /**
     * Sets the "AbstractText" element
     */
    void setAbstractText(java.lang.String abstractText);

    /**
     * Sets (as xml) the "AbstractText" element
     */
    void xsetAbstractText(org.apache.xmlbeans.XmlString abstractText);
}
