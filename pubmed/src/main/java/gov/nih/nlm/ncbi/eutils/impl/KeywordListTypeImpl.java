/*
 * XML Type:  KeywordListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.KeywordListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML KeywordListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class KeywordListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.KeywordListType {
    private static final long serialVersionUID = 1L;

    public KeywordListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Keyword"),
        new QName("", "Owner"),
    };


    /**
     * Gets a List of "Keyword" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.KeywordType> getKeywordList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getKeywordArray,
                this::setKeywordArray,
                this::insertNewKeyword,
                this::removeKeyword,
                this::sizeOfKeywordArray
            );
        }
    }

    /**
     * Gets array of all "Keyword" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordType[] getKeywordArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.KeywordType[0]);
    }

    /**
     * Gets ith "Keyword" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordType getKeywordArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordType target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Keyword" element
     */
    @Override
    public int sizeOfKeywordArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Keyword" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setKeywordArray(gov.nih.nlm.ncbi.eutils.KeywordType[] keywordArray) {
        check_orphaned();
        arraySetterHelper(keywordArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Keyword" element
     */
    @Override
    public void setKeywordArray(int i, gov.nih.nlm.ncbi.eutils.KeywordType keyword) {
        generatedSetterHelperImpl(keyword, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Keyword" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordType insertNewKeyword(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordType target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Keyword" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordType addNewKeyword() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordType target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Keyword" element
     */
    @Override
    public void removeKeyword(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /**
     * Gets the "Owner" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordListType.Owner.Enum getOwner() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.KeywordListType.Owner.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "Owner" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordListType.Owner xgetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordListType.Owner target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordListType.Owner)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.KeywordListType.Owner)get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return target;
        }
    }

    /**
     * True if has "Owner" attribute
     */
    @Override
    public boolean isSetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
        }
    }

    /**
     * Sets the "Owner" attribute
     */
    @Override
    public void setOwner(gov.nih.nlm.ncbi.eutils.KeywordListType.Owner.Enum owner) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setEnumValue(owner);
        }
    }

    /**
     * Sets (as xml) the "Owner" attribute
     */
    @Override
    public void xsetOwner(gov.nih.nlm.ncbi.eutils.KeywordListType.Owner owner) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordListType.Owner target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordListType.Owner)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.KeywordListType.Owner)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(owner);
        }
    }

    /**
     * Unsets the "Owner" attribute
     */
    @Override
    public void unsetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[1]);
        }
    }
    /**
     * An XML Owner(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.KeywordListType$Owner.
     */
    public static class OwnerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.KeywordListType.Owner {
        private static final long serialVersionUID = 1L;

        public OwnerImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected OwnerImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
