/*
 * An XML document type.
 * Localname: NameOfSubstance
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.NameOfSubstanceDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one NameOfSubstance(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface NameOfSubstanceDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.NameOfSubstanceDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "nameofsubstance85c1doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "NameOfSubstance" element
     */
    java.lang.String getNameOfSubstance();

    /**
     * Gets (as xml) the "NameOfSubstance" element
     */
    org.apache.xmlbeans.XmlString xgetNameOfSubstance();

    /**
     * Sets the "NameOfSubstance" element
     */
    void setNameOfSubstance(java.lang.String nameOfSubstance);

    /**
     * Sets (as xml) the "NameOfSubstance" element
     */
    void xsetNameOfSubstance(org.apache.xmlbeans.XmlString nameOfSubstance);
}
