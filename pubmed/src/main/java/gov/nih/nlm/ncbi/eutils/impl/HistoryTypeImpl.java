/*
 * XML Type:  HistoryType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.HistoryType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML HistoryType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class HistoryTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.HistoryType {
    private static final long serialVersionUID = 1L;

    public HistoryTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PubMedPubDate"),
    };


    /**
     * Gets a List of "PubMedPubDate" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.PubMedPubDateType> getPubMedPubDateList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getPubMedPubDateArray,
                this::setPubMedPubDateArray,
                this::insertNewPubMedPubDate,
                this::removePubMedPubDate,
                this::sizeOfPubMedPubDateArray
            );
        }
    }

    /**
     * Gets array of all "PubMedPubDate" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubMedPubDateType[] getPubMedPubDateArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.PubMedPubDateType[0]);
    }

    /**
     * Gets ith "PubMedPubDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubMedPubDateType getPubMedPubDateArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubMedPubDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubMedPubDateType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "PubMedPubDate" element
     */
    @Override
    public int sizeOfPubMedPubDateArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "PubMedPubDate" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setPubMedPubDateArray(gov.nih.nlm.ncbi.eutils.PubMedPubDateType[] pubMedPubDateArray) {
        check_orphaned();
        arraySetterHelper(pubMedPubDateArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "PubMedPubDate" element
     */
    @Override
    public void setPubMedPubDateArray(int i, gov.nih.nlm.ncbi.eutils.PubMedPubDateType pubMedPubDate) {
        generatedSetterHelperImpl(pubMedPubDate, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PubMedPubDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubMedPubDateType insertNewPubMedPubDate(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubMedPubDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubMedPubDateType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "PubMedPubDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubMedPubDateType addNewPubMedPubDate() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubMedPubDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubMedPubDateType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "PubMedPubDate" element
     */
    @Override
    public void removePubMedPubDate(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
