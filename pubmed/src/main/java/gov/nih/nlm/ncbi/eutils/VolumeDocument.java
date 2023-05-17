/*
 * An XML document type.
 * Localname: Volume
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.VolumeDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Volume(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface VolumeDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.VolumeDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "volumecc23doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Volume" element
     */
    java.lang.String getVolume();

    /**
     * Gets (as xml) the "Volume" element
     */
    org.apache.xmlbeans.XmlString xgetVolume();

    /**
     * Sets the "Volume" element
     */
    void setVolume(java.lang.String volume);

    /**
     * Sets (as xml) the "Volume" element
     */
    void xsetVolume(org.apache.xmlbeans.XmlString volume);
}
