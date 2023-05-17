/*
 * XML Type:  ChemicalListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ChemicalListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ChemicalListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ChemicalListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ChemicalListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "chemicallisttype4d63type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Chemical" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.ChemicalType> getChemicalList();

    /**
     * Gets array of all "Chemical" elements
     */
    gov.nih.nlm.ncbi.eutils.ChemicalType[] getChemicalArray();

    /**
     * Gets ith "Chemical" element
     */
    gov.nih.nlm.ncbi.eutils.ChemicalType getChemicalArray(int i);

    /**
     * Returns number of "Chemical" element
     */
    int sizeOfChemicalArray();

    /**
     * Sets array of all "Chemical" element
     */
    void setChemicalArray(gov.nih.nlm.ncbi.eutils.ChemicalType[] chemicalArray);

    /**
     * Sets ith "Chemical" element
     */
    void setChemicalArray(int i, gov.nih.nlm.ncbi.eutils.ChemicalType chemical);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Chemical" element
     */
    gov.nih.nlm.ncbi.eutils.ChemicalType insertNewChemical(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Chemical" element
     */
    gov.nih.nlm.ncbi.eutils.ChemicalType addNewChemical();

    /**
     * Removes the ith "Chemical" element
     */
    void removeChemical(int i);
}
