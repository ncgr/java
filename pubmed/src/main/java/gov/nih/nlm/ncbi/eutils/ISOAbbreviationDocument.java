/*
 * An XML document type.
 * Localname: ISOAbbreviation
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ISOAbbreviationDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ISOAbbreviation(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface ISOAbbreviationDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ISOAbbreviationDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "isoabbreviation98fcdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ISOAbbreviation" element
     */
    java.lang.String getISOAbbreviation();

    /**
     * Gets (as xml) the "ISOAbbreviation" element
     */
    org.apache.xmlbeans.XmlString xgetISOAbbreviation();

    /**
     * Sets the "ISOAbbreviation" element
     */
    void setISOAbbreviation(java.lang.String isoAbbreviation);

    /**
     * Sets (as xml) the "ISOAbbreviation" element
     */
    void xsetISOAbbreviation(org.apache.xmlbeans.XmlString isoAbbreviation);
}
