/*
 * XML Type:  DataBankType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DataBankType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML DataBankType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class DataBankTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.DataBankType {
    private static final long serialVersionUID = 1L;

    public DataBankTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "DataBankName"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "AccessionNumberList"),
    };


    /**
     * Gets the "DataBankName" element
     */
    @Override
    public java.lang.String getDataBankName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "DataBankName" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetDataBankName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "DataBankName" element
     */
    @Override
    public void setDataBankName(java.lang.String dataBankName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(dataBankName);
        }
    }

    /**
     * Sets (as xml) the "DataBankName" element
     */
    @Override
    public void xsetDataBankName(org.apache.xmlbeans.XmlString dataBankName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(dataBankName);
        }
    }

    /**
     * Gets the "AccessionNumberList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.AccessionNumberListType getAccessionNumberList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.AccessionNumberListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.AccessionNumberListType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "AccessionNumberList" element
     */
    @Override
    public boolean isSetAccessionNumberList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "AccessionNumberList" element
     */
    @Override
    public void setAccessionNumberList(gov.nih.nlm.ncbi.eutils.AccessionNumberListType accessionNumberList) {
        generatedSetterHelperImpl(accessionNumberList, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "AccessionNumberList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.AccessionNumberListType addNewAccessionNumberList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.AccessionNumberListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.AccessionNumberListType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Unsets the "AccessionNumberList" element
     */
    @Override
    public void unsetAccessionNumberList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }
}
