/*
 * XML Type:  QualifierNameType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.QualifierNameType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML QualifierNameType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.QualifierNameType.
 */
public class QualifierNameTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.QualifierNameType {
    private static final long serialVersionUID = 1L;

    public QualifierNameTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType, true);
    }

    protected QualifierNameTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
        super(sType, b);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "MajorTopicYN"),
    };


    /**
     * Gets the "MajorTopicYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN.Enum getMajorTopicYN() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "MajorTopicYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN xgetMajorTopicYN() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN)get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return target;
        }
    }

    /**
     * True if has "MajorTopicYN" attribute
     */
    @Override
    public boolean isSetMajorTopicYN() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
        }
    }

    /**
     * Sets the "MajorTopicYN" attribute
     */
    @Override
    public void setMajorTopicYN(gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN.Enum majorTopicYN) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setEnumValue(majorTopicYN);
        }
    }

    /**
     * Sets (as xml) the "MajorTopicYN" attribute
     */
    @Override
    public void xsetMajorTopicYN(gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN majorTopicYN) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(majorTopicYN);
        }
    }

    /**
     * Unsets the "MajorTopicYN" attribute
     */
    @Override
    public void unsetMajorTopicYN() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }
    /**
     * An XML MajorTopicYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.QualifierNameType$MajorTopicYN.
     */
    public static class MajorTopicYNImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.QualifierNameType.MajorTopicYN {
        private static final long serialVersionUID = 1L;

        public MajorTopicYNImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected MajorTopicYNImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
