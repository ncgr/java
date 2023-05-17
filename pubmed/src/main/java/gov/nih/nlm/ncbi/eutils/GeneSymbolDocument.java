/*
 * An XML document type.
 * Localname: GeneSymbol
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GeneSymbolDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one GeneSymbol(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface GeneSymbolDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.GeneSymbolDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "genesymbol21d0doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "GeneSymbol" element
     */
    java.lang.String getGeneSymbol();

    /**
     * Gets (as xml) the "GeneSymbol" element
     */
    org.apache.xmlbeans.XmlString xgetGeneSymbol();

    /**
     * Sets the "GeneSymbol" element
     */
    void setGeneSymbol(java.lang.String geneSymbol);

    /**
     * Sets (as xml) the "GeneSymbol" element
     */
    void xsetGeneSymbol(org.apache.xmlbeans.XmlString geneSymbol);
}
