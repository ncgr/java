/*
 * An XML document type.
 * Localname: Affiliation
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AffiliationDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Affiliation(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface AffiliationDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AffiliationDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "affiliation25dfdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Affiliation" element
     */
    java.lang.String getAffiliation();

    /**
     * Gets (as xml) the "Affiliation" element
     */
    org.apache.xmlbeans.XmlString xgetAffiliation();

    /**
     * Sets the "Affiliation" element
     */
    void setAffiliation(java.lang.String affiliation);

    /**
     * Sets (as xml) the "Affiliation" element
     */
    void xsetAffiliation(org.apache.xmlbeans.XmlString affiliation);
}
