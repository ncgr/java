/*
 * An XML document type.
 * Localname: PubmedArticleSet
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one PubmedArticleSet(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface PubmedArticleSetDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubmedarticlesetf042doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "PubmedArticleSet" element
     */
    gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet getPubmedArticleSet();

    /**
     * Sets the "PubmedArticleSet" element
     */
    void setPubmedArticleSet(gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet pubmedArticleSet);

    /**
     * Appends and returns a new empty "PubmedArticleSet" element
     */
    gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet addNewPubmedArticleSet();

    /**
     * An XML PubmedArticleSet(@http://www.ncbi.nlm.nih.gov/eutils).
     *
     * This is a complex type.
     */
    public interface PubmedArticleSet extends org.apache.xmlbeans.XmlObject {
        ElementFactory<gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubmedarticleset1459elemtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        /**
         * Gets a List of "PubmedArticle" elements
         */
        java.util.List<gov.nih.nlm.ncbi.eutils.PubmedArticleType> getPubmedArticleList();

        /**
         * Gets array of all "PubmedArticle" elements
         */
        gov.nih.nlm.ncbi.eutils.PubmedArticleType[] getPubmedArticleArray();

        /**
         * Gets ith "PubmedArticle" element
         */
        gov.nih.nlm.ncbi.eutils.PubmedArticleType getPubmedArticleArray(int i);

        /**
         * Returns number of "PubmedArticle" element
         */
        int sizeOfPubmedArticleArray();

        /**
         * Sets array of all "PubmedArticle" element
         */
        void setPubmedArticleArray(gov.nih.nlm.ncbi.eutils.PubmedArticleType[] pubmedArticleArray);

        /**
         * Sets ith "PubmedArticle" element
         */
        void setPubmedArticleArray(int i, gov.nih.nlm.ncbi.eutils.PubmedArticleType pubmedArticle);

        /**
         * Inserts and returns a new empty value (as xml) as the ith "PubmedArticle" element
         */
        gov.nih.nlm.ncbi.eutils.PubmedArticleType insertNewPubmedArticle(int i);

        /**
         * Appends and returns a new empty value (as xml) as the last "PubmedArticle" element
         */
        gov.nih.nlm.ncbi.eutils.PubmedArticleType addNewPubmedArticle();

        /**
         * Removes the ith "PubmedArticle" element
         */
        void removePubmedArticle(int i);
    }
}
