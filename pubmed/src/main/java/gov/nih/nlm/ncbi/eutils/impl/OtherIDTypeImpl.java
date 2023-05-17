/*
 * XML Type:  OtherIDType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.OtherIDType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML OtherIDType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.OtherIDType.
 */
public class OtherIDTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.OtherIDType {
    private static final long serialVersionUID = 1L;

    public OtherIDTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType, true);
    }

    protected OtherIDTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
        super(sType, b);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "Source"),
    };


    /**
     * Gets the "Source" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherIDType.Source.Enum getSource() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.OtherIDType.Source.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "Source" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherIDType.Source xgetSource() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherIDType.Source target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherIDType.Source)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "Source" attribute
     */
    @Override
    public void setSource(gov.nih.nlm.ncbi.eutils.OtherIDType.Source.Enum source) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setEnumValue(source);
        }
    }

    /**
     * Sets (as xml) the "Source" attribute
     */
    @Override
    public void xsetSource(gov.nih.nlm.ncbi.eutils.OtherIDType.Source source) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherIDType.Source target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherIDType.Source)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.OtherIDType.Source)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(source);
        }
    }
    /**
     * An XML Source(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.OtherIDType$Source.
     */
    public static class SourceImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.OtherIDType.Source {
        private static final long serialVersionUID = 1L;

        public SourceImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected SourceImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
