/*
 * XML Type:  MedlineJournalInfoType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML MedlineJournalInfoType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class MedlineJournalInfoTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType {
    private static final long serialVersionUID = 1L;

    public MedlineJournalInfoTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Country"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "MedlineTA"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "NlmUniqueID"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ISSNLinking"),
    };


    /**
     * Gets the "Country" element
     */
    @Override
    public java.lang.String getCountry() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Country" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetCountry() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * True if has "Country" element
     */
    @Override
    public boolean isSetCountry() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    /**
     * Sets the "Country" element
     */
    @Override
    public void setCountry(java.lang.String country) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(country);
        }
    }

    /**
     * Sets (as xml) the "Country" element
     */
    @Override
    public void xsetCountry(org.apache.xmlbeans.XmlString country) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(country);
        }
    }

    /**
     * Unsets the "Country" element
     */
    @Override
    public void unsetCountry() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /**
     * Gets the "MedlineTA" element
     */
    @Override
    public java.lang.String getMedlineTA() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "MedlineTA" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetMedlineTA() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * Sets the "MedlineTA" element
     */
    @Override
    public void setMedlineTA(java.lang.String medlineTA) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(medlineTA);
        }
    }

    /**
     * Sets (as xml) the "MedlineTA" element
     */
    @Override
    public void xsetMedlineTA(org.apache.xmlbeans.XmlString medlineTA) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(medlineTA);
        }
    }

    /**
     * Gets the "NlmUniqueID" element
     */
    @Override
    public java.lang.String getNlmUniqueID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "NlmUniqueID" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetNlmUniqueID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target;
        }
    }

    /**
     * True if has "NlmUniqueID" element
     */
    @Override
    public boolean isSetNlmUniqueID() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    /**
     * Sets the "NlmUniqueID" element
     */
    @Override
    public void setNlmUniqueID(java.lang.String nlmUniqueID) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(nlmUniqueID);
        }
    }

    /**
     * Sets (as xml) the "NlmUniqueID" element
     */
    @Override
    public void xsetNlmUniqueID(org.apache.xmlbeans.XmlString nlmUniqueID) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.set(nlmUniqueID);
        }
    }

    /**
     * Unsets the "NlmUniqueID" element
     */
    @Override
    public void unsetNlmUniqueID() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /**
     * Gets the "ISSNLinking" element
     */
    @Override
    public java.lang.String getISSNLinking() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "ISSNLinking" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetISSNLinking() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target;
        }
    }

    /**
     * True if has "ISSNLinking" element
     */
    @Override
    public boolean isSetISSNLinking() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    /**
     * Sets the "ISSNLinking" element
     */
    @Override
    public void setISSNLinking(java.lang.String issnLinking) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[3]);
            }
            target.setStringValue(issnLinking);
        }
    }

    /**
     * Sets (as xml) the "ISSNLinking" element
     */
    @Override
    public void xsetISSNLinking(org.apache.xmlbeans.XmlString issnLinking) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[3]);
            }
            target.set(issnLinking);
        }
    }

    /**
     * Unsets the "ISSNLinking" element
     */
    @Override
    public void unsetISSNLinking() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }
}
