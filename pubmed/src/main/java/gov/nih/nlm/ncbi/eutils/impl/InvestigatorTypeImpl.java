/*
 * XML Type:  InvestigatorType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.InvestigatorType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML InvestigatorType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class InvestigatorTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.InvestigatorType {
    private static final long serialVersionUID = 1L;

    public InvestigatorTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "LastName"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ForeName"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Initials"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Suffix"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "NameID"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Affiliation"),
        new QName("", "ValidYN"),
    };


    /**
     * Gets the "LastName" element
     */
    @Override
    public java.lang.String getLastName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "LastName" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetLastName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "LastName" element
     */
    @Override
    public void setLastName(java.lang.String lastName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(lastName);
        }
    }

    /**
     * Sets (as xml) the "LastName" element
     */
    @Override
    public void xsetLastName(org.apache.xmlbeans.XmlString lastName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(lastName);
        }
    }

    /**
     * Gets the "ForeName" element
     */
    @Override
    public java.lang.String getForeName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "ForeName" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetForeName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * True if has "ForeName" element
     */
    @Override
    public boolean isSetForeName() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "ForeName" element
     */
    @Override
    public void setForeName(java.lang.String foreName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(foreName);
        }
    }

    /**
     * Sets (as xml) the "ForeName" element
     */
    @Override
    public void xsetForeName(org.apache.xmlbeans.XmlString foreName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(foreName);
        }
    }

    /**
     * Unsets the "ForeName" element
     */
    @Override
    public void unsetForeName() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /**
     * Gets the "Initials" element
     */
    @Override
    public java.lang.String getInitials() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Initials" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetInitials() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target;
        }
    }

    /**
     * True if has "Initials" element
     */
    @Override
    public boolean isSetInitials() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    /**
     * Sets the "Initials" element
     */
    @Override
    public void setInitials(java.lang.String initials) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(initials);
        }
    }

    /**
     * Sets (as xml) the "Initials" element
     */
    @Override
    public void xsetInitials(org.apache.xmlbeans.XmlString initials) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[2]);
            }
            target.set(initials);
        }
    }

    /**
     * Unsets the "Initials" element
     */
    @Override
    public void unsetInitials() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /**
     * Gets the "Suffix" element
     */
    @Override
    public java.lang.String getSuffix() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Suffix" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetSuffix() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target;
        }
    }

    /**
     * True if has "Suffix" element
     */
    @Override
    public boolean isSetSuffix() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    /**
     * Sets the "Suffix" element
     */
    @Override
    public void setSuffix(java.lang.String suffix) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[3]);
            }
            target.setStringValue(suffix);
        }
    }

    /**
     * Sets (as xml) the "Suffix" element
     */
    @Override
    public void xsetSuffix(org.apache.xmlbeans.XmlString suffix) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[3]);
            }
            target.set(suffix);
        }
    }

    /**
     * Unsets the "Suffix" element
     */
    @Override
    public void unsetSuffix() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /**
     * Gets a List of "NameID" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.NameIDType> getNameIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getNameIDArray,
                this::setNameIDArray,
                this::insertNewNameID,
                this::removeNameID,
                this::sizeOfNameIDArray
            );
        }
    }

    /**
     * Gets array of all "NameID" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.NameIDType[] getNameIDArray() {
        return getXmlObjectArray(PROPERTY_QNAME[4], new gov.nih.nlm.ncbi.eutils.NameIDType[0]);
    }

    /**
     * Gets ith "NameID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.NameIDType getNameIDArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.NameIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.NameIDType)get_store().find_element_user(PROPERTY_QNAME[4], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "NameID" element
     */
    @Override
    public int sizeOfNameIDArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    /**
     * Sets array of all "NameID" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setNameIDArray(gov.nih.nlm.ncbi.eutils.NameIDType[] nameIDArray) {
        check_orphaned();
        arraySetterHelper(nameIDArray, PROPERTY_QNAME[4]);
    }

    /**
     * Sets ith "NameID" element
     */
    @Override
    public void setNameIDArray(int i, gov.nih.nlm.ncbi.eutils.NameIDType nameID) {
        generatedSetterHelperImpl(nameID, PROPERTY_QNAME[4], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "NameID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.NameIDType insertNewNameID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.NameIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.NameIDType)get_store().insert_element_user(PROPERTY_QNAME[4], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "NameID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.NameIDType addNewNameID() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.NameIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.NameIDType)get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /**
     * Removes the ith "NameID" element
     */
    @Override
    public void removeNameID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /**
     * Gets the "Affiliation" element
     */
    @Override
    public java.lang.String getAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Affiliation" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target;
        }
    }

    /**
     * True if has "Affiliation" element
     */
    @Override
    public boolean isSetAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    /**
     * Sets the "Affiliation" element
     */
    @Override
    public void setAffiliation(java.lang.String affiliation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.setStringValue(affiliation);
        }
    }

    /**
     * Sets (as xml) the "Affiliation" element
     */
    @Override
    public void xsetAffiliation(org.apache.xmlbeans.XmlString affiliation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.set(affiliation);
        }
    }

    /**
     * Unsets the "Affiliation" element
     */
    @Override
    public void unsetAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /**
     * Gets the "ValidYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN.Enum getValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[6]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "ValidYN" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN xgetValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN)get_default_attribute_value(PROPERTY_QNAME[6]);
            }
            return target;
        }
    }

    /**
     * True if has "ValidYN" attribute
     */
    @Override
    public boolean isSetValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[6]) != null;
        }
    }

    /**
     * Sets the "ValidYN" attribute
     */
    @Override
    public void setValidYN(gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN.Enum validYN) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.setEnumValue(validYN);
        }
    }

    /**
     * Sets (as xml) the "ValidYN" attribute
     */
    @Override
    public void xsetValidYN(gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN validYN) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.set(validYN);
        }
    }

    /**
     * Unsets the "ValidYN" attribute
     */
    @Override
    public void unsetValidYN() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[6]);
        }
    }
    /**
     * An XML ValidYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.InvestigatorType$ValidYN.
     */
    public static class ValidYNImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.InvestigatorType.ValidYN {
        private static final long serialVersionUID = 1L;

        public ValidYNImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected ValidYNImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
