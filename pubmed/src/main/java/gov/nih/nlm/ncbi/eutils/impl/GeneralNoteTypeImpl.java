/*
 * XML Type:  GeneralNoteType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GeneralNoteType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML GeneralNoteType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.GeneralNoteType.
 */
public class GeneralNoteTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.GeneralNoteType {
    private static final long serialVersionUID = 1L;

    public GeneralNoteTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType, true);
    }

    protected GeneralNoteTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
        super(sType, b);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "Owner"),
    };


    /**
     * Gets the "Owner" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner.Enum getOwner() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "Owner" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner xgetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner)get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return target;
        }
    }

    /**
     * True if has "Owner" attribute
     */
    @Override
    public boolean isSetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
        }
    }

    /**
     * Sets the "Owner" attribute
     */
    @Override
    public void setOwner(gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner.Enum owner) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setEnumValue(owner);
        }
    }

    /**
     * Sets (as xml) the "Owner" attribute
     */
    @Override
    public void xsetOwner(gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner owner) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(owner);
        }
    }

    /**
     * Unsets the "Owner" attribute
     */
    @Override
    public void unsetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }
    /**
     * An XML Owner(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.GeneralNoteType$Owner.
     */
    public static class OwnerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner {
        private static final long serialVersionUID = 1L;

        public OwnerImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected OwnerImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
