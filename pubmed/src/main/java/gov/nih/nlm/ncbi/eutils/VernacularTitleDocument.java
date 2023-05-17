/*
 * An XML document type.
 * Localname: VernacularTitle
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.VernacularTitleDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one VernacularTitle(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface VernacularTitleDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.VernacularTitleDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "vernaculartitlec12cdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "VernacularTitle" element
     */
    java.lang.String getVernacularTitle();

    /**
     * Gets (as xml) the "VernacularTitle" element
     */
    org.apache.xmlbeans.XmlString xgetVernacularTitle();

    /**
     * Sets the "VernacularTitle" element
     */
    void setVernacularTitle(java.lang.String vernacularTitle);

    /**
     * Sets (as xml) the "VernacularTitle" element
     */
    void xsetVernacularTitle(org.apache.xmlbeans.XmlString vernacularTitle);
}
