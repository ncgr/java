/*
 * An XML document type.
 * Localname: Agency
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AgencyDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Agency(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface AgencyDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AgencyDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "agencyd758doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Agency" element
     */
    java.lang.String getAgency();

    /**
     * Gets (as xml) the "Agency" element
     */
    org.apache.xmlbeans.XmlString xgetAgency();

    /**
     * Sets the "Agency" element
     */
    void setAgency(java.lang.String agency);

    /**
     * Sets (as xml) the "Agency" element
     */
    void xsetAgency(org.apache.xmlbeans.XmlString agency);
}
