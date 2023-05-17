/*
 * XML Type:  JournalIssueType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.JournalIssueType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML JournalIssueType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class JournalIssueTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.JournalIssueType {
    private static final long serialVersionUID = 1L;

    public JournalIssueTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Volume"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Issue"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PubDate"),
        new QName("", "CitedMedium"),
    };


    /**
     * Gets the "Volume" element
     */
    @Override
    public java.lang.String getVolume() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Volume" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetVolume() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * True if has "Volume" element
     */
    @Override
    public boolean isSetVolume() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    /**
     * Sets the "Volume" element
     */
    @Override
    public void setVolume(java.lang.String volume) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(volume);
        }
    }

    /**
     * Sets (as xml) the "Volume" element
     */
    @Override
    public void xsetVolume(org.apache.xmlbeans.XmlString volume) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(volume);
        }
    }

    /**
     * Unsets the "Volume" element
     */
    @Override
    public void unsetVolume() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /**
     * Gets the "Issue" element
     */
    @Override
    public java.lang.String getIssue() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Issue" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetIssue() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * True if has "Issue" element
     */
    @Override
    public boolean isSetIssue() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "Issue" element
     */
    @Override
    public void setIssue(java.lang.String issue) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(issue);
        }
    }

    /**
     * Sets (as xml) the "Issue" element
     */
    @Override
    public void xsetIssue(org.apache.xmlbeans.XmlString issue) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(issue);
        }
    }

    /**
     * Unsets the "Issue" element
     */
    @Override
    public void unsetIssue() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /**
     * Gets the "PubDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubDateType getPubDate() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubDateType)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "PubDate" element
     */
    @Override
    public void setPubDate(gov.nih.nlm.ncbi.eutils.PubDateType pubDate) {
        generatedSetterHelperImpl(pubDate, PROPERTY_QNAME[2], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "PubDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubDateType addNewPubDate() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubDateType)get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Gets the "CitedMedium" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium.Enum getCitedMedium() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "CitedMedium" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium xgetCitedMedium() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium target = null;
            target = (gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Sets the "CitedMedium" attribute
     */
    @Override
    public void setCitedMedium(gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium.Enum citedMedium) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.setEnumValue(citedMedium);
        }
    }

    /**
     * Sets (as xml) the "CitedMedium" attribute
     */
    @Override
    public void xsetCitedMedium(gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium citedMedium) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium target = null;
            target = (gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set(citedMedium);
        }
    }
    /**
     * An XML CitedMedium(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.JournalIssueType$CitedMedium.
     */
    public static class CitedMediumImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium {
        private static final long serialVersionUID = 1L;

        public CitedMediumImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected CitedMediumImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
