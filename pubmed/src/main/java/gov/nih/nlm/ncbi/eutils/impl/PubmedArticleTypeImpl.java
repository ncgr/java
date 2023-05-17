/*
 * XML Type:  PubmedArticleType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubmedArticleType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML PubmedArticleType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class PubmedArticleTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PubmedArticleType {
    private static final long serialVersionUID = 1L;

    public PubmedArticleTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "MedlineCitation"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PubmedData"),
    };


    /**
     * Gets the "MedlineCitation" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationType getMedlineCitation() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "MedlineCitation" element
     */
    @Override
    public boolean isSetMedlineCitation() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    /**
     * Sets the "MedlineCitation" element
     */
    @Override
    public void setMedlineCitation(gov.nih.nlm.ncbi.eutils.MedlineCitationType medlineCitation) {
        generatedSetterHelperImpl(medlineCitation, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "MedlineCitation" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationType addNewMedlineCitation() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Unsets the "MedlineCitation" element
     */
    @Override
    public void unsetMedlineCitation() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /**
     * Gets the "PubmedData" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubmedDataType getPubmedData() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubmedDataType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubmedDataType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "PubmedData" element
     */
    @Override
    public boolean isSetPubmedData() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "PubmedData" element
     */
    @Override
    public void setPubmedData(gov.nih.nlm.ncbi.eutils.PubmedDataType pubmedData) {
        generatedSetterHelperImpl(pubmedData, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "PubmedData" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubmedDataType addNewPubmedData() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubmedDataType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubmedDataType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Unsets the "PubmedData" element
     */
    @Override
    public void unsetPubmedData() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }
}
