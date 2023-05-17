/*
 * XML Type:  CommentsCorrectionsType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML CommentsCorrectionsType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class CommentsCorrectionsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType {
    private static final long serialVersionUID = 1L;

    public CommentsCorrectionsTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "RefSource"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PMID"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Note"),
        new QName("", "RefType"),
    };


    /**
     * Gets the "RefSource" element
     */
    @Override
    public java.lang.String getRefSource() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "RefSource" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetRefSource() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "RefSource" element
     */
    @Override
    public void setRefSource(java.lang.String refSource) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(refSource);
        }
    }

    /**
     * Sets (as xml) the "RefSource" element
     */
    @Override
    public void xsetRefSource(org.apache.xmlbeans.XmlString refSource) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(refSource);
        }
    }

    /**
     * Gets the "PMID" element
     */
    @Override
    public java.lang.String getPMID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "PMID" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetPMID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * True if has "PMID" element
     */
    @Override
    public boolean isSetPMID() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "PMID" element
     */
    @Override
    public void setPMID(java.lang.String pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(pmid);
        }
    }

    /**
     * Sets (as xml) the "PMID" element
     */
    @Override
    public void xsetPMID(org.apache.xmlbeans.XmlString pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(pmid);
        }
    }

    /**
     * Unsets the "PMID" element
     */
    @Override
    public void unsetPMID() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /**
     * Gets the "Note" element
     */
    @Override
    public java.lang.String getNote() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Note" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetNote() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target;
        }
    }

    /**
     * True if has "Note" element
     */
    @Override
    public boolean isSetNote() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    /**
     * Sets the "Note" element
     */
    @Override
    public void setNote(java.lang.String note) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(note);
        }
    }

    /**
     * Sets (as xml) the "Note" element
     */
    @Override
    public void xsetNote(org.apache.xmlbeans.XmlString note) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.set(note);
        }
    }

    /**
     * Unsets the "Note" element
     */
    @Override
    public void unsetNote() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /**
     * Gets the "RefType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType.Enum getRefType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "RefType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType xgetRefType() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Sets the "RefType" attribute
     */
    @Override
    public void setRefType(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType.Enum refType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.setEnumValue(refType);
        }
    }

    /**
     * Sets (as xml) the "RefType" attribute
     */
    @Override
    public void xsetRefType(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType refType) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set(refType);
        }
    }
    /**
     * An XML RefType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType$RefType.
     */
    public static class RefTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType {
        private static final long serialVersionUID = 1L;

        public RefTypeImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected RefTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
