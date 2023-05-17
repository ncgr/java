/*
 * XML Type:  PubMedPubDateType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubMedPubDateType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML PubMedPubDateType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class PubMedPubDateTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PubMedPubDateType {
    private static final long serialVersionUID = 1L;

    public PubMedPubDateTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Year"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Month"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Day"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Hour"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Minute"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Second"),
        new QName("", "PubStatus"),
    };


    /**
     * Gets the "Year" element
     */
    @Override
    public java.lang.String getYear() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Year" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetYear() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "Year" element
     */
    @Override
    public void setYear(java.lang.String year) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(year);
        }
    }

    /**
     * Sets (as xml) the "Year" element
     */
    @Override
    public void xsetYear(org.apache.xmlbeans.XmlString year) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(year);
        }
    }

    /**
     * Gets the "Month" element
     */
    @Override
    public java.lang.String getMonth() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Month" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetMonth() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * Sets the "Month" element
     */
    @Override
    public void setMonth(java.lang.String month) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(month);
        }
    }

    /**
     * Sets (as xml) the "Month" element
     */
    @Override
    public void xsetMonth(org.apache.xmlbeans.XmlString month) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(month);
        }
    }

    /**
     * Gets the "Day" element
     */
    @Override
    public java.lang.String getDay() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Day" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetDay() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target;
        }
    }

    /**
     * Sets the "Day" element
     */
    @Override
    public void setDay(java.lang.String day) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(day);
        }
    }

    /**
     * Sets (as xml) the "Day" element
     */
    @Override
    public void xsetDay(org.apache.xmlbeans.XmlString day) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.set(day);
        }
    }

    /**
     * Gets the "Hour" element
     */
    @Override
    public java.lang.String getHour() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Hour" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetHour() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target;
        }
    }

    /**
     * True if has "Hour" element
     */
    @Override
    public boolean isSetHour() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    /**
     * Sets the "Hour" element
     */
    @Override
    public void setHour(java.lang.String hour) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[3]);
            }
            target.setStringValue(hour);
        }
    }

    /**
     * Sets (as xml) the "Hour" element
     */
    @Override
    public void xsetHour(org.apache.xmlbeans.XmlString hour) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[3]);
            }
            target.set(hour);
        }
    }

    /**
     * Unsets the "Hour" element
     */
    @Override
    public void unsetHour() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /**
     * Gets the "Minute" element
     */
    @Override
    public java.lang.String getMinute() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Minute" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetMinute() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return target;
        }
    }

    /**
     * True if has "Minute" element
     */
    @Override
    public boolean isSetMinute() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    /**
     * Sets the "Minute" element
     */
    @Override
    public void setMinute(java.lang.String minute) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[4], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[4]);
            }
            target.setStringValue(minute);
        }
    }

    /**
     * Sets (as xml) the "Minute" element
     */
    @Override
    public void xsetMinute(org.apache.xmlbeans.XmlString minute) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[4], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[4]);
            }
            target.set(minute);
        }
    }

    /**
     * Unsets the "Minute" element
     */
    @Override
    public void unsetMinute() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /**
     * Gets the "Second" element
     */
    @Override
    public java.lang.String getSecond() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Second" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetSecond() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target;
        }
    }

    /**
     * True if has "Second" element
     */
    @Override
    public boolean isSetSecond() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    /**
     * Sets the "Second" element
     */
    @Override
    public void setSecond(java.lang.String second) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.setStringValue(second);
        }
    }

    /**
     * Sets (as xml) the "Second" element
     */
    @Override
    public void xsetSecond(org.apache.xmlbeans.XmlString second) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.set(second);
        }
    }

    /**
     * Unsets the "Second" element
     */
    @Override
    public void unsetSecond() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /**
     * Gets the "PubStatus" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus.Enum getPubStatus() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "PubStatus" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus xgetPubStatus() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /**
     * Sets the "PubStatus" attribute
     */
    @Override
    public void setPubStatus(gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus.Enum pubStatus) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.setEnumValue(pubStatus);
        }
    }

    /**
     * Sets (as xml) the "PubStatus" attribute
     */
    @Override
    public void xsetPubStatus(gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus pubStatus) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.set(pubStatus);
        }
    }
    /**
     * An XML PubStatus(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.PubMedPubDateType$PubStatus.
     */
    public static class PubStatusImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus {
        private static final long serialVersionUID = 1L;

        public PubStatusImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected PubStatusImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
