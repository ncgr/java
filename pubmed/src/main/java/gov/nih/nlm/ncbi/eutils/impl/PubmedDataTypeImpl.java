/*
 * XML Type:  PubmedDataType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubmedDataType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML PubmedDataType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class PubmedDataTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PubmedDataType {
    private static final long serialVersionUID = 1L;

    public PubmedDataTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "History"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PublicationStatus"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ArticleIdList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ObjectList"),
    };


    /**
     * Gets the "History" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.HistoryType getHistory() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.HistoryType target = null;
            target = (gov.nih.nlm.ncbi.eutils.HistoryType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "History" element
     */
    @Override
    public boolean isSetHistory() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    /**
     * Sets the "History" element
     */
    @Override
    public void setHistory(gov.nih.nlm.ncbi.eutils.HistoryType history) {
        generatedSetterHelperImpl(history, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "History" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.HistoryType addNewHistory() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.HistoryType target = null;
            target = (gov.nih.nlm.ncbi.eutils.HistoryType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Unsets the "History" element
     */
    @Override
    public void unsetHistory() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /**
     * Gets the "PublicationStatus" element
     */
    @Override
    public java.lang.String getPublicationStatus() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "PublicationStatus" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetPublicationStatus() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * Sets the "PublicationStatus" element
     */
    @Override
    public void setPublicationStatus(java.lang.String publicationStatus) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(publicationStatus);
        }
    }

    /**
     * Sets (as xml) the "PublicationStatus" element
     */
    @Override
    public void xsetPublicationStatus(org.apache.xmlbeans.XmlString publicationStatus) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(publicationStatus);
        }
    }

    /**
     * Gets the "ArticleIdList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdListType getArticleIdList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdListType)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ArticleIdList" element
     */
    @Override
    public void setArticleIdList(gov.nih.nlm.ncbi.eutils.ArticleIdListType articleIdList) {
        generatedSetterHelperImpl(articleIdList, PROPERTY_QNAME[2], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ArticleIdList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdListType addNewArticleIdList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdListType)get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Gets the "ObjectList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ObjectListType getObjectList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ObjectListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ObjectListType)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "ObjectList" element
     */
    @Override
    public boolean isSetObjectList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    /**
     * Sets the "ObjectList" element
     */
    @Override
    public void setObjectList(gov.nih.nlm.ncbi.eutils.ObjectListType objectList) {
        generatedSetterHelperImpl(objectList, PROPERTY_QNAME[3], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ObjectList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ObjectListType addNewObjectList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ObjectListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ObjectListType)get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Unsets the "ObjectList" element
     */
    @Override
    public void unsetObjectList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }
}
