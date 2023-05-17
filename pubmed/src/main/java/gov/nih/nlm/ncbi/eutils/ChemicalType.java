/*
 * XML Type:  ChemicalType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ChemicalType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ChemicalType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ChemicalType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ChemicalType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "chemicaltypeb661type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "RegistryNumber" element
     */
    java.lang.String getRegistryNumber();

    /**
     * Gets (as xml) the "RegistryNumber" element
     */
    org.apache.xmlbeans.XmlString xgetRegistryNumber();

    /**
     * Sets the "RegistryNumber" element
     */
    void setRegistryNumber(java.lang.String registryNumber);

    /**
     * Sets (as xml) the "RegistryNumber" element
     */
    void xsetRegistryNumber(org.apache.xmlbeans.XmlString registryNumber);

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
