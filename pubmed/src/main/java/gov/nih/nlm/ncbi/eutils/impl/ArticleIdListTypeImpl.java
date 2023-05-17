/*
 * XML Type:  ArticleIdListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleIdListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ArticleIdListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class ArticleIdListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.ArticleIdListType {
    private static final long serialVersionUID = 1L;

    public ArticleIdListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ArticleId"),
    };


    /**
     * Gets a List of "ArticleId" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.ArticleIdType> getArticleIdList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getArticleIdArray,
                this::setArticleIdArray,
                this::insertNewArticleId,
                this::removeArticleId,
                this::sizeOfArticleIdArray
            );
        }
    }

    /**
     * Gets array of all "ArticleId" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdType[] getArticleIdArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.ArticleIdType[0]);
    }

    /**
     * Gets ith "ArticleId" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdType getArticleIdArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ArticleId" element
     */
    @Override
    public int sizeOfArticleIdArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ArticleId" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setArticleIdArray(gov.nih.nlm.ncbi.eutils.ArticleIdType[] articleIdArray) {
        check_orphaned();
        arraySetterHelper(articleIdArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ArticleId" element
     */
    @Override
    public void setArticleIdArray(int i, gov.nih.nlm.ncbi.eutils.ArticleIdType articleId) {
        generatedSetterHelperImpl(articleId, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ArticleId" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdType insertNewArticleId(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ArticleId" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdType addNewArticleId() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ArticleId" element
     */
    @Override
    public void removeArticleId(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
