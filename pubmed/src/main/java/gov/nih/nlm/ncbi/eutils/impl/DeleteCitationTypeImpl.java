/*
 * XML Type:  DeleteCitationType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DeleteCitationType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML DeleteCitationType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class DeleteCitationTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.DeleteCitationType {
    private static final long serialVersionUID = 1L;

    public DeleteCitationTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PMID"),
    };


    /**
     * Gets a List of "PMID" elements
     */
    @Override
    public java.util.List<java.lang.String> getPMIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getPMIDArray,
                this::setPMIDArray,
                this::insertPMID,
                this::removePMID,
                this::sizeOfPMIDArray
            );
        }
    }

    /**
     * Gets array of all "PMID" elements
     */
    @Override
    public java.lang.String[] getPMIDArray() {
        return getObjectArray(PROPERTY_QNAME[0], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "PMID" element
     */
    @Override
    public java.lang.String getPMIDArray(int i) {
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
     * Gets (as xml) a List of "PMID" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetPMIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetPMIDArray,
                this::xsetPMIDArray,
                this::insertNewPMID,
                this::removePMID,
                this::sizeOfPMIDArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "PMID" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetPMIDArray() {
        return xgetArray(PROPERTY_QNAME[0], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "PMID" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetPMIDArray(int i) {
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
     * Returns number of "PMID" element
     */
    @Override
    public int sizeOfPMIDArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "PMID" element
     */
    @Override
    public void setPMIDArray(java.lang.String[] pmidArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(pmidArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets ith "PMID" element
     */
    @Override
    public void setPMIDArray(int i, java.lang.String pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(pmid);
        }
    }

    /**
     * Sets (as xml) array of all "PMID" element
     */
    @Override
    public void xsetPMIDArray(org.apache.xmlbeans.XmlString[]pmidArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(pmidArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets (as xml) ith "PMID" element
     */
    @Override
    public void xsetPMIDArray(int i, org.apache.xmlbeans.XmlString pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(pmid);
        }
    }

    /**
     * Inserts the value as the ith "PMID" element
     */
    @Override
    public void insertPMID(int i, java.lang.String pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            target.setStringValue(pmid);
        }
    }

    /**
     * Appends the value as the last "PMID" element
     */
    @Override
    public void addPMID(java.lang.String pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            target.setStringValue(pmid);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PMID" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewPMID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "PMID" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewPMID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "PMID" element
     */
    @Override
    public void removePMID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
