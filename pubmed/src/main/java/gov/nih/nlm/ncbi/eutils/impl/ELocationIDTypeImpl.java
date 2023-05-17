/*
 * XML Type:  ELocationIDType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ELocationIDType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ELocationIDType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ELocationIDType.
 */
public class ELocationIDTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.ELocationIDType {
    private static final long serialVersionUID = 1L;

    public ELocationIDTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType, true);
    }

    protected ELocationIDTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
        super(sType, b);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "EIdType"),
        new QName("", "ValidYN"),
    };


    /**
     * Gets the "EIdType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType.Enum getEIdType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "EIdType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType xgetEIdType() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "EIdType" attribute
     */
    @Override
    public void setEIdType(gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType.Enum eIdType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setEnumValue(eIdType);
        }
    }

    /**
     * Sets (as xml) the "EIdType" attribute
     */
    @Override
    public void xsetEIdType(gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType eIdType) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(eIdType);
        }
    }

    /**
     * Gets the "ValidYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN.Enum getValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "ValidYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN xgetValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN)get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return target;
        }
    }

    /**
     * True if has "ValidYN" attribute
     */
    @Override
    public boolean isSetValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
        }
    }

    /**
     * Sets the "ValidYN" attribute
     */
    @Override
    public void setValidYN(gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN.Enum validYN) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setEnumValue(validYN);
        }
    }

    /**
     * Sets (as xml) the "ValidYN" attribute
     */
    @Override
    public void xsetValidYN(gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN validYN) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(validYN);
        }
    }

    /**
     * Unsets the "ValidYN" attribute
     */
    @Override
    public void unsetValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[1]);
        }
    }
    /**
     * An XML EIdType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ELocationIDType$EIdType.
     */
    public static class EIdTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType {
        private static final long serialVersionUID = 1L;

        public EIdTypeImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected EIdTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
    /**
     * An XML ValidYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ELocationIDType$ValidYN.
     */
    public static class ValidYNImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN {
        private static final long serialVersionUID = 1L;

        public ValidYNImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected ValidYNImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
