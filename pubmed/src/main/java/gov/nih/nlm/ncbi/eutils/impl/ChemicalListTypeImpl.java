/*
 * XML Type:  ChemicalListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ChemicalListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ChemicalListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class ChemicalListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.ChemicalListType {
    private static final long serialVersionUID = 1L;

    public ChemicalListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Chemical"),
    };


    /**
     * Gets a List of "Chemical" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.ChemicalType> getChemicalList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getChemicalArray,
                this::setChemicalArray,
                this::insertNewChemical,
                this::removeChemical,
                this::sizeOfChemicalArray
            );
        }
    }

    /**
     * Gets array of all "Chemical" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ChemicalType[] getChemicalArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.ChemicalType[0]);
    }

    /**
     * Gets ith "Chemical" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ChemicalType getChemicalArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ChemicalType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ChemicalType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Chemical" element
     */
    @Override
    public int sizeOfChemicalArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Chemical" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setChemicalArray(gov.nih.nlm.ncbi.eutils.ChemicalType[] chemicalArray) {
        check_orphaned();
        arraySetterHelper(chemicalArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Chemical" element
     */
    @Override
    public void setChemicalArray(int i, gov.nih.nlm.ncbi.eutils.ChemicalType chemical) {
        generatedSetterHelperImpl(chemical, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Chemical" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ChemicalType insertNewChemical(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ChemicalType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ChemicalType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Chemical" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ChemicalType addNewChemical() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ChemicalType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ChemicalType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Chemical" element
     */
    @Override
    public void removeChemical(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
