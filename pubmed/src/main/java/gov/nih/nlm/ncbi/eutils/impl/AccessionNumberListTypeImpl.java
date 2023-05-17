/*
 * XML Type:  AccessionNumberListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AccessionNumberListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML AccessionNumberListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class AccessionNumberListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.AccessionNumberListType {
    private static final long serialVersionUID = 1L;

    public AccessionNumberListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "AccessionNumber"),
    };


    /**
     * Gets a List of "AccessionNumber" elements
     */
    @Override
    public java.util.List<java.lang.String> getAccessionNumberList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getAccessionNumberArray,
                this::setAccessionNumberArray,
                this::insertAccessionNumber,
                this::removeAccessionNumber,
                this::sizeOfAccessionNumberArray
            );
        }
    }

    /**
     * Gets array of all "AccessionNumber" elements
     */
    @Override
    public java.lang.String[] getAccessionNumberArray() {
        return getObjectArray(PROPERTY_QNAME[0], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "AccessionNumber" element
     */
    @Override
    public java.lang.String getAccessionNumberArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /**
     * Gets (as xml) a List of "AccessionNumber" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetAccessionNumberList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetAccessionNumberArray,
                this::xsetAccessionNumberArray,
                this::insertNewAccessionNumber,
                this::removeAccessionNumber,
                this::sizeOfAccessionNumberArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "AccessionNumber" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetAccessionNumberArray() {
        return xgetArray(PROPERTY_QNAME[0], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "AccessionNumber" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetAccessionNumberArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "AccessionNumber" element
     */
    @Override
    public int sizeOfAccessionNumberArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "AccessionNumber" element
     */
    @Override
    public void setAccessionNumberArray(java.lang.String[] accessionNumberArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(accessionNumberArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets ith "AccessionNumber" element
     */
    @Override
    public void setAccessionNumberArray(int i, java.lang.String accessionNumber) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(accessionNumber);
        }
    }

    /**
     * Sets (as xml) array of all "AccessionNumber" element
     */
    @Override
    public void xsetAccessionNumberArray(org.apache.xmlbeans.XmlString[]accessionNumberArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(accessionNumberArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets (as xml) ith "AccessionNumber" element
     */
    @Override
    public void xsetAccessionNumberArray(int i, org.apache.xmlbeans.XmlString accessionNumber) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(accessionNumber);
        }
    }

    /**
     * Inserts the value as the ith "AccessionNumber" element
     */
    @Override
    public void insertAccessionNumber(int i, java.lang.String accessionNumber) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            target.setStringValue(accessionNumber);
        }
    }

    /**
     * Appends the value as the last "AccessionNumber" element
     */
    @Override
    public void addAccessionNumber(java.lang.String accessionNumber) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            target.setStringValue(accessionNumber);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "AccessionNumber" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewAccessionNumber(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "AccessionNumber" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewAccessionNumber() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "AccessionNumber" element
     */
    @Override
    public void removeAccessionNumber(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
