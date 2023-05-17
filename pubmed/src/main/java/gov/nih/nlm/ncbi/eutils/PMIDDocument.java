/*
 * An XML document type.
 * Localname: PMID
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PMIDDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one PMID(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface PMIDDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PMIDDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pmid2085doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "PMID" element
     */
    java.lang.String getPMID();

    /**
     * Gets (as xml) the "PMID" element
     */
    org.apache.xmlbeans.XmlString xgetPMID();

    /**
     * Sets the "PMID" element
     */
    void setPMID(java.lang.String pmid);

    /**
     * Sets (as xml) the "PMID" element
     */
    void xsetPMID(org.apache.xmlbeans.XmlString pmid);
}
