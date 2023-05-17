/*
 * An XML document type.
 * Localname: Suffix
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.SuffixDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Suffix(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface SuffixDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.SuffixDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "suffixd88cdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Suffix" element
     */
    java.lang.String getSuffix();

    /**
     * Gets (as xml) the "Suffix" element
     */
    org.apache.xmlbeans.XmlString xgetSuffix();

    /**
     * Sets the "Suffix" element
     */
    void setSuffix(java.lang.String suffix);

    /**
     * Sets (as xml) the "Suffix" element
     */
    void xsetSuffix(org.apache.xmlbeans.XmlString suffix);
}
