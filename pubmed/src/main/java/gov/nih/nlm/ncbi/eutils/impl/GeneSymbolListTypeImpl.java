/*
 * XML Type:  GeneSymbolListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GeneSymbolListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML GeneSymbolListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class GeneSymbolListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.GeneSymbolListType {
    private static final long serialVersionUID = 1L;

    public GeneSymbolListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "GeneSymbol"),
    };


    /**
     * Gets a List of "GeneSymbol" elements
     */
    @Override
    public java.util.List<java.lang.String> getGeneSymbolList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getGeneSymbolArray,
                this::setGeneSymbolArray,
                this::insertGeneSymbol,
                this::removeGeneSymbol,
                this::sizeOfGeneSymbolArray
            );
        }
    }

    /**
     * Gets array of all "GeneSymbol" elements
     */
    @Override
    public java.lang.String[] getGeneSymbolArray() {
        return getObjectArray(PROPERTY_QNAME[0], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "GeneSymbol" element
     */
    @Override
    public java.lang.String getGeneSymbolArray(int i) {
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
     * Gets (as xml) a List of "GeneSymbol" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetGeneSymbolList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetGeneSymbolArray,
                this::xsetGeneSymbolArray,
                this::insertNewGeneSymbol,
                this::removeGeneSymbol,
                this::sizeOfGeneSymbolArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "GeneSymbol" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetGeneSymbolArray() {
        return xgetArray(PROPERTY_QNAME[0], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "GeneSymbol" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetGeneSymbolArray(int i) {
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
     * Returns number of "GeneSymbol" element
     */
    @Override
    public int sizeOfGeneSymbolArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "GeneSymbol" element
     */
    @Override
    public void setGeneSymbolArray(java.lang.String[] geneSymbolArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(geneSymbolArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets ith "GeneSymbol" element
     */
    @Override
    public void setGeneSymbolArray(int i, java.lang.String geneSymbol) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(geneSymbol);
        }
    }

    /**
     * Sets (as xml) array of all "GeneSymbol" element
     */
    @Override
    public void xsetGeneSymbolArray(org.apache.xmlbeans.XmlString[]geneSymbolArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(geneSymbolArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets (as xml) ith "GeneSymbol" element
     */
    @Override
    public void xsetGeneSymbolArray(int i, org.apache.xmlbeans.XmlString geneSymbol) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(geneSymbol);
        }
    }

    /**
     * Inserts the value as the ith "GeneSymbol" element
     */
    @Override
    public void insertGeneSymbol(int i, java.lang.String geneSymbol) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            target.setStringValue(geneSymbol);
        }
    }

    /**
     * Appends the value as the last "GeneSymbol" element
     */
    @Override
    public void addGeneSymbol(java.lang.String geneSymbol) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            target.setStringValue(geneSymbol);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "GeneSymbol" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewGeneSymbol(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "GeneSymbol" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewGeneSymbol() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "GeneSymbol" element
     */
    @Override
    public void removeGeneSymbol(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
