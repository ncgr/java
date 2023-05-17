/*
 * XML Type:  GeneSymbolListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GeneSymbolListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML GeneSymbolListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface GeneSymbolListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.GeneSymbolListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "genesymbollisttype0bc8type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "GeneSymbol" elements
     */
    java.util.List<java.lang.String> getGeneSymbolList();

    /**
     * Gets array of all "GeneSymbol" elements
     */
    java.lang.String[] getGeneSymbolArray();

    /**
     * Gets ith "GeneSymbol" element
     */
    java.lang.String getGeneSymbolArray(int i);

    /**
     * Gets (as xml) a List of "GeneSymbol" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetGeneSymbolList();

    /**
     * Gets (as xml) array of all "GeneSymbol" elements
     */
    org.apache.xmlbeans.XmlString[] xgetGeneSymbolArray();

    /**
     * Gets (as xml) ith "GeneSymbol" element
     */
    org.apache.xmlbeans.XmlString xgetGeneSymbolArray(int i);

    /**
     * Returns number of "GeneSymbol" element
     */
    int sizeOfGeneSymbolArray();

    /**
     * Sets array of all "GeneSymbol" element
     */
    void setGeneSymbolArray(java.lang.String[] geneSymbolArray);

    /**
     * Sets ith "GeneSymbol" element
     */
    void setGeneSymbolArray(int i, java.lang.String geneSymbol);

    /**
     * Sets (as xml) array of all "GeneSymbol" element
     */
    void xsetGeneSymbolArray(org.apache.xmlbeans.XmlString[] geneSymbolArray);

    /**
     * Sets (as xml) ith "GeneSymbol" element
     */
    void xsetGeneSymbolArray(int i, org.apache.xmlbeans.XmlString geneSymbol);

    /**
     * Inserts the value as the ith "GeneSymbol" element
     */
    void insertGeneSymbol(int i, java.lang.String geneSymbol);

    /**
     * Appends the value as the last "GeneSymbol" element
     */
    void addGeneSymbol(java.lang.String geneSymbol);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "GeneSymbol" element
     */
    org.apache.xmlbeans.XmlString insertNewGeneSymbol(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "GeneSymbol" element
     */
    org.apache.xmlbeans.XmlString addNewGeneSymbol();

    /**
     * Removes the ith "GeneSymbol" element
     */
    void removeGeneSymbol(int i);
}
