/*
 * XML Type:  InvestigatorListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.InvestigatorListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML InvestigatorListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class InvestigatorListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.InvestigatorListType {
    private static final long serialVersionUID = 1L;

    public InvestigatorListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Investigator"),
    };


    /**
     * Gets a List of "Investigator" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.InvestigatorType> getInvestigatorList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getInvestigatorArray,
                this::setInvestigatorArray,
                this::insertNewInvestigator,
                this::removeInvestigator,
                this::sizeOfInvestigatorArray
            );
        }
    }

    /**
     * Gets array of all "Investigator" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorType[] getInvestigatorArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.InvestigatorType[0]);
    }

    /**
     * Gets ith "Investigator" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorType getInvestigatorArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorType target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Investigator" element
     */
    @Override
    public int sizeOfInvestigatorArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Investigator" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setInvestigatorArray(gov.nih.nlm.ncbi.eutils.InvestigatorType[] investigatorArray) {
        check_orphaned();
        arraySetterHelper(investigatorArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Investigator" element
     */
    @Override
    public void setInvestigatorArray(int i, gov.nih.nlm.ncbi.eutils.InvestigatorType investigator) {
        generatedSetterHelperImpl(investigator, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Investigator" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorType insertNewInvestigator(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorType target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Investigator" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorType addNewInvestigator() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorType target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Investigator" element
     */
    @Override
    public void removeInvestigator(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
