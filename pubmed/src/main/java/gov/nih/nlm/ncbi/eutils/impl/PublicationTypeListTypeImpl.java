/*
 * XML Type:  PublicationTypeListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PublicationTypeListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML PublicationTypeListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class PublicationTypeListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PublicationTypeListType {
    private static final long serialVersionUID = 1L;

    public PublicationTypeListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PublicationType"),
    };


    /**
     * Gets a List of "PublicationType" elements
     */
    @Override
    public java.util.List<java.lang.String> getPublicationTypeList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getPublicationTypeArray,
                this::setPublicationTypeArray,
                this::insertPublicationType,
                this::removePublicationType,
                this::sizeOfPublicationTypeArray
            );
        }
    }

    /**
     * Gets array of all "PublicationType" elements
     */
    @Override
    public java.lang.String[] getPublicationTypeArray() {
        return getObjectArray(PROPERTY_QNAME[0], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "PublicationType" element
     */
    @Override
    public java.lang.String getPublicationTypeArray(int i) {
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
     * Gets (as xml) a List of "PublicationType" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetPublicationTypeList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetPublicationTypeArray,
                this::xsetPublicationTypeArray,
                this::insertNewPublicationType,
                this::removePublicationType,
                this::sizeOfPublicationTypeArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "PublicationType" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetPublicationTypeArray() {
        return xgetArray(PROPERTY_QNAME[0], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "PublicationType" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetPublicationTypeArray(int i) {
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
     * Returns number of "PublicationType" element
     */
    @Override
    public int sizeOfPublicationTypeArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "PublicationType" element
     */
    @Override
    public void setPublicationTypeArray(java.lang.String[] publicationTypeArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(publicationTypeArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets ith "PublicationType" element
     */
    @Override
    public void setPublicationTypeArray(int i, java.lang.String publicationType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(publicationType);
        }
    }

    /**
     * Sets (as xml) array of all "PublicationType" element
     */
    @Override
    public void xsetPublicationTypeArray(org.apache.xmlbeans.XmlString[]publicationTypeArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(publicationTypeArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets (as xml) ith "PublicationType" element
     */
    @Override
    public void xsetPublicationTypeArray(int i, org.apache.xmlbeans.XmlString publicationType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(publicationType);
        }
    }

    /**
     * Inserts the value as the ith "PublicationType" element
     */
    @Override
    public void insertPublicationType(int i, java.lang.String publicationType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            target.setStringValue(publicationType);
        }
    }

    /**
     * Appends the value as the last "PublicationType" element
     */
    @Override
    public void addPublicationType(java.lang.String publicationType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            target.setStringValue(publicationType);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PublicationType" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewPublicationType(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "PublicationType" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewPublicationType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "PublicationType" element
     */
    @Override
    public void removePublicationType(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
