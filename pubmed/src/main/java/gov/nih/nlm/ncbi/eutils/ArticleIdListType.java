/*
 * XML Type:  ArticleIdListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleIdListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ArticleIdListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ArticleIdListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ArticleIdListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "articleidlisttypeac96type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ArticleId" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.ArticleIdType> getArticleIdList();

    /**
     * Gets array of all "ArticleId" elements
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdType[] getArticleIdArray();

    /**
     * Gets ith "ArticleId" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdType getArticleIdArray(int i);

    /**
     * Returns number of "ArticleId" element
     */
    int sizeOfArticleIdArray();

    /**
     * Sets array of all "ArticleId" element
     */
    void setArticleIdArray(gov.nih.nlm.ncbi.eutils.ArticleIdType[] articleIdArray);

    /**
     * Sets ith "ArticleId" element
     */
    void setArticleIdArray(int i, gov.nih.nlm.ncbi.eutils.ArticleIdType articleId);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ArticleId" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdType insertNewArticleId(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ArticleId" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdType addNewArticleId();

    /**
     * Removes the ith "ArticleId" element
     */
    void removeArticleId(int i);
}
