/*
 * An XML document type.
 * Localname: CollectiveName
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CollectiveNameDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CollectiveName(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface CollectiveNameDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.CollectiveNameDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "collectivenameccc4doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CollectiveName" element
     */
    java.lang.String getCollectiveName();

    /**
     * Gets (as xml) the "CollectiveName" element
     */
    org.apache.xmlbeans.XmlString xgetCollectiveName();

    /**
     * Sets the "CollectiveName" element
     */
    void setCollectiveName(java.lang.String collectiveName);

    /**
     * Sets (as xml) the "CollectiveName" element
     */
    void xsetCollectiveName(org.apache.xmlbeans.XmlString collectiveName);
}
