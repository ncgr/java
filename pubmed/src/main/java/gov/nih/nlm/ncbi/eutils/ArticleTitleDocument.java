/*
 * An XML document type.
 * Localname: ArticleTitle
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleTitleDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ArticleTitle(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface ArticleTitleDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ArticleTitleDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "articletitle59fbdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ArticleTitle" element
     */
    java.lang.String getArticleTitle();

    /**
     * Gets (as xml) the "ArticleTitle" element
     */
    org.apache.xmlbeans.XmlString xgetArticleTitle();

    /**
     * Sets the "ArticleTitle" element
     */
    void setArticleTitle(java.lang.String articleTitle);

    /**
     * Sets (as xml) the "ArticleTitle" element
     */
    void xsetArticleTitle(org.apache.xmlbeans.XmlString articleTitle);
}
