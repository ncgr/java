/*
 * XML Type:  ISSNType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ISSNType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ISSNType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ISSNType.
 */
public class ISSNTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.ISSNType {
    private static final long serialVersionUID = 1L;

    public ISSNTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType, true);
    }

    protected ISSNTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
        super(sType, b);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "IssnType"),
    };


    /**
     * Gets the "IssnType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ISSNType.IssnType.Enum getIssnType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.ISSNType.IssnType.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "IssnType" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ISSNType.IssnType xgetIssnType() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ISSNType.IssnType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ISSNType.IssnType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "IssnType" attribute
     */
    @Override
    public void setIssnType(gov.nih.nlm.ncbi.eutils.ISSNType.IssnType.Enum issnType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setEnumValue(issnType);
        }
    }

    /**
     * Sets (as xml) the "IssnType" attribute
     */
    @Override
    public void xsetIssnType(gov.nih.nlm.ncbi.eutils.ISSNType.IssnType issnType) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ISSNType.IssnType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ISSNType.IssnType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ISSNType.IssnType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(issnType);
        }
    }
    /**
     * An XML IssnType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ISSNType$IssnType.
     */
    public static class IssnTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.ISSNType.IssnType {
        private static final long serialVersionUID = 1L;

        public IssnTypeImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected IssnTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
