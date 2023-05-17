/*
 * XML Type:  PubmedArticleType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubmedArticleType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PubmedArticleType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PubmedArticleType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PubmedArticleType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubmedarticletypeb23etype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "MedlineCitation" element
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationType getMedlineCitation();

    /**
     * True if has "MedlineCitation" element
     */
    boolean isSetMedlineCitation();

    /**
     * Sets the "MedlineCitation" element
     */
    void setMedlineCitation(gov.nih.nlm.ncbi.eutils.MedlineCitationType medlineCitation);

    /**
     * Appends and returns a new empty "MedlineCitation" element
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationType addNewMedlineCitation();

    /**
     * Unsets the "MedlineCitation" element
     */
    void unsetMedlineCitation();

    /**
     * Gets the "PubmedData" element
     */
    gov.nih.nlm.ncbi.eutils.PubmedDataType getPubmedData();

    /**
     * True if has "PubmedData" element
     */
    boolean isSetPubmedData();

    /**
     * Sets the "PubmedData" element
     */
    void setPubmedData(gov.nih.nlm.ncbi.eutils.PubmedDataType pubmedData);

    /**
     * Appends and returns a new empty "PubmedData" element
     */
    gov.nih.nlm.ncbi.eutils.PubmedDataType addNewPubmedData();

    /**
     * Unsets the "PubmedData" element
     */
    void unsetPubmedData();
}
