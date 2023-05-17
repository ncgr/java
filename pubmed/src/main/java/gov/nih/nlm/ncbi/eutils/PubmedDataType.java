/*
 * XML Type:  PubmedDataType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubmedDataType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PubmedDataType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PubmedDataType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PubmedDataType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubmeddatatype2cdatype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "History" element
     */
    gov.nih.nlm.ncbi.eutils.HistoryType getHistory();

    /**
     * True if has "History" element
     */
    boolean isSetHistory();

    /**
     * Sets the "History" element
     */
    void setHistory(gov.nih.nlm.ncbi.eutils.HistoryType history);

    /**
     * Appends and returns a new empty "History" element
     */
    gov.nih.nlm.ncbi.eutils.HistoryType addNewHistory();

    /**
     * Unsets the "History" element
     */
    void unsetHistory();

    /**
     * Gets the "PublicationStatus" element
     */
    java.lang.String getPublicationStatus();

    /**
     * Gets (as xml) the "PublicationStatus" element
     */
    org.apache.xmlbeans.XmlString xgetPublicationStatus();

    /**
     * Sets the "PublicationStatus" element
     */
    void setPublicationStatus(java.lang.String publicationStatus);

    /**
     * Sets (as xml) the "PublicationStatus" element
     */
    void xsetPublicationStatus(org.apache.xmlbeans.XmlString publicationStatus);

    /**
     * Gets the "ArticleIdList" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdListType getArticleIdList();

    /**
     * Sets the "ArticleIdList" element
     */
    void setArticleIdList(gov.nih.nlm.ncbi.eutils.ArticleIdListType articleIdList);

    /**
     * Appends and returns a new empty "ArticleIdList" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdListType addNewArticleIdList();

    /**
     * Gets the "ObjectList" element
     */
    gov.nih.nlm.ncbi.eutils.ObjectListType getObjectList();

    /**
     * True if has "ObjectList" element
     */
    boolean isSetObjectList();

    /**
     * Sets the "ObjectList" element
     */
    void setObjectList(gov.nih.nlm.ncbi.eutils.ObjectListType objectList);

    /**
     * Appends and returns a new empty "ObjectList" element
     */
    gov.nih.nlm.ncbi.eutils.ObjectListType addNewObjectList();

    /**
     * Unsets the "ObjectList" element
     */
    void unsetObjectList();
}
