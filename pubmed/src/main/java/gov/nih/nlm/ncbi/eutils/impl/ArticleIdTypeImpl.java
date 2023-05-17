/*
 * XML Type:  ArticleIdType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleIdType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ArticleIdType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ArticleIdType.
 */
public class ArticleIdTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.ArticleIdType {
    private static final long serialVersionUID = 1L;

    public ArticleIdTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType, true);
    }

    protected ArticleIdTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
        super(sType, b);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "IdType"),
    };


    /**
     * Gets the "IdType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType.Enum getIdType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "IdType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType xgetIdType() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType)get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return target;
        }
    }

    /**
     * True if has "IdType" attribute
     */
    @Override
    public boolean isSetIdType() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
        }
    }

    /**
     * Sets the "IdType" attribute
     */
    @Override
    public void setIdType(gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType.Enum idType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setEnumValue(idType);
        }
    }

    /**
     * Sets (as xml) the "IdType" attribute
     */
    @Override
    public void xsetIdType(gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType idType) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(idType);
        }
    }

    /**
     * Unsets the "IdType" attribute
     */
    @Override
    public void unsetIdType() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }
    /**
     * An XML IdType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ArticleIdType$IdType.
     */
    public static class IdTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType {
        private static final long serialVersionUID = 1L;

        public IdTypeImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected IdTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
