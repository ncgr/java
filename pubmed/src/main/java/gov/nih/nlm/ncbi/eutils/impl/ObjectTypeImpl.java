/*
 * XML Type:  ObjectType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ObjectType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ObjectType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class ObjectTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.ObjectType {
    private static final long serialVersionUID = 1L;

    public ObjectTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Param"),
        new QName("", "Type"),
    };


    /**
     * Gets a List of "Param" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.ParamType> getParamList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getParamArray,
                this::setParamArray,
                this::insertNewParam,
                this::removeParam,
                this::sizeOfParamArray
            );
        }
    }

    /**
     * Gets array of all "Param" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ParamType[] getParamArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.ParamType[0]);
    }

    /**
     * Gets ith "Param" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ParamType getParamArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ParamType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ParamType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Param" element
     */
    @Override
    public int sizeOfParamArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Param" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setParamArray(gov.nih.nlm.ncbi.eutils.ParamType[] paramArray) {
        check_orphaned();
        arraySetterHelper(paramArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Param" element
     */
    @Override
    public void setParamArray(int i, gov.nih.nlm.ncbi.eutils.ParamType param) {
        generatedSetterHelperImpl(param, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Param" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ParamType insertNewParam(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ParamType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ParamType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Param" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ParamType addNewParam() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ParamType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ParamType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Param" element
     */
    @Override
    public void removeParam(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /**
     * Gets the "Type" attribute
     */
    @Override
    public java.lang.String getType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Type" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "Type" attribute
     */
    @Override
    public void setType(java.lang.String type) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(type);
        }
    }

    /**
     * Sets (as xml) the "Type" attribute
     */
    @Override
    public void xsetType(org.apache.xmlbeans.XmlString type) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(type);
        }
    }
}
