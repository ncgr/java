/*
 * An XML document type.
 * Localname: NumberOfReferences
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.NumberOfReferencesDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one NumberOfReferences(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface NumberOfReferencesDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.NumberOfReferencesDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "numberofreferences2855doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "NumberOfReferences" element
     */
    java.lang.String getNumberOfReferences();

    /**
     * Gets (as xml) the "NumberOfReferences" element
     */
    org.apache.xmlbeans.XmlString xgetNumberOfReferences();

    /**
     * Sets the "NumberOfReferences" element
     */
    void setNumberOfReferences(java.lang.String numberOfReferences);

    /**
     * Sets (as xml) the "NumberOfReferences" element
     */
    void xsetNumberOfReferences(org.apache.xmlbeans.XmlString numberOfReferences);
}
