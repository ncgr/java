/*
 * XML Type:  GrantListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GrantListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML GrantListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class GrantListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.GrantListType {
    private static final long serialVersionUID = 1L;

    public GrantListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Grant"),
        new QName("", "CompleteYN"),
    };


    /**
     * Gets a List of "Grant" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.GrantType> getGrantList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getGrantArray,
                this::setGrantArray,
                this::insertNewGrant,
                this::removeGrant,
                this::sizeOfGrantArray
            );
        }
    }

    /**
     * Gets array of all "Grant" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantType[] getGrantArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.GrantType[0]);
    }

    /**
     * Gets ith "Grant" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantType getGrantArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Grant" element
     */
    @Override
    public int sizeOfGrantArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Grant" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setGrantArray(gov.nih.nlm.ncbi.eutils.GrantType[] grantArray) {
        check_orphaned();
        arraySetterHelper(grantArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Grant" element
     */
    @Override
    public void setGrantArray(int i, gov.nih.nlm.ncbi.eutils.GrantType grant) {
        generatedSetterHelperImpl(grant, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Grant" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantType insertNewGrant(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Grant" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantType addNewGrant() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Grant" element
     */
    @Override
    public void removeGrant(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /**
     * Gets the "CompleteYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN.Enum getCompleteYN() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "CompleteYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN xgetCompleteYN() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN)get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return target;
        }
    }

    /**
     * True if has "CompleteYN" attribute
     */
    @Override
    public boolean isSetCompleteYN() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
        }
    }

    /**
     * Sets the "CompleteYN" attribute
     */
    @Override
    public void setCompleteYN(gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN.Enum completeYN) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setEnumValue(completeYN);
        }
    }

    /**
     * Sets (as xml) the "CompleteYN" attribute
     */
    @Override
    public void xsetCompleteYN(gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN completeYN) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(completeYN);
        }
    }

    /**
     * Unsets the "CompleteYN" attribute
     */
    @Override
    public void unsetCompleteYN() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[1]);
        }
    }
    /**
     * An XML CompleteYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.GrantListType$CompleteYN.
     */
    public static class CompleteYNImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.GrantListType.CompleteYN {
        private static final long serialVersionUID = 1L;

        public CompleteYNImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected CompleteYNImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
