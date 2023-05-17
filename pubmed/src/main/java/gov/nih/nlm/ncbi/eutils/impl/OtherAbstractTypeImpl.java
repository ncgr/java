/*
 * XML Type:  OtherAbstractType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.OtherAbstractType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML OtherAbstractType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class OtherAbstractTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.OtherAbstractType {
    private static final long serialVersionUID = 1L;

    public OtherAbstractTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "AbstractText"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "CopyrightInformation"),
        new QName("", "Type"),
    };


    /**
     * Gets the "AbstractText" element
     */
    @Override
    public java.lang.String getAbstractText() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "AbstractText" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetAbstractText() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "AbstractText" element
     */
    @Override
    public void setAbstractText(java.lang.String abstractText) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(abstractText);
        }
    }

    /**
     * Sets (as xml) the "AbstractText" element
     */
    @Override
    public void xsetAbstractText(org.apache.xmlbeans.XmlString abstractText) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(abstractText);
        }
    }

    /**
     * Gets the "CopyrightInformation" element
     */
    @Override
    public java.lang.String getCopyrightInformation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "CopyrightInformation" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetCopyrightInformation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * True if has "CopyrightInformation" element
     */
    @Override
    public boolean isSetCopyrightInformation() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "CopyrightInformation" element
     */
    @Override
    public void setCopyrightInformation(java.lang.String copyrightInformation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(copyrightInformation);
        }
    }

    /**
     * Sets (as xml) the "CopyrightInformation" element
     */
    @Override
    public void xsetCopyrightInformation(org.apache.xmlbeans.XmlString copyrightInformation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(copyrightInformation);
        }
    }

    /**
     * Unsets the "CopyrightInformation" element
     */
    @Override
    public void unsetCopyrightInformation() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /**
     * Gets the "Type" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type.Enum getType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "Type" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type xgetType() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Sets the "Type" attribute
     */
    @Override
    public void setType(gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type.Enum type) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.setEnumValue(type);
        }
    }

    /**
     * Sets (as xml) the "Type" attribute
     */
    @Override
    public void xsetType(gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type type) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set(type);
        }
    }
    /**
     * An XML Type(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.OtherAbstractType$Type.
     */
    public static class TypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type {
        private static final long serialVersionUID = 1L;

        public TypeImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected TypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
