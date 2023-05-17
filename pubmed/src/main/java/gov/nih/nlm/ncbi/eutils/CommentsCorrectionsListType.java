/*
 * XML Type:  CommentsCorrectionsListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommentsCorrectionsListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface CommentsCorrectionsListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "commentscorrectionslisttype75c6type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "CommentsCorrections" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType> getCommentsCorrectionsList();

    /**
     * Gets array of all "CommentsCorrections" elements
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType[] getCommentsCorrectionsArray();

    /**
     * Gets ith "CommentsCorrections" element
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType getCommentsCorrectionsArray(int i);

    /**
     * Returns number of "CommentsCorrections" element
     */
    int sizeOfCommentsCorrectionsArray();

    /**
     * Sets array of all "CommentsCorrections" element
     */
    void setCommentsCorrectionsArray(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType[] commentsCorrectionsArray);

    /**
     * Sets ith "CommentsCorrections" element
     */
    void setCommentsCorrectionsArray(int i, gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType commentsCorrections);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "CommentsCorrections" element
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType insertNewCommentsCorrections(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "CommentsCorrections" element
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType addNewCommentsCorrections();

    /**
     * Removes the ith "CommentsCorrections" element
     */
    void removeCommentsCorrections(int i);
}
