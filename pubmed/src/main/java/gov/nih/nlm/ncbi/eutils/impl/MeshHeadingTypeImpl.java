/*
 * XML Type:  MeshHeadingType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MeshHeadingType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML MeshHeadingType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class MeshHeadingTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.MeshHeadingType {
    private static final long serialVersionUID = 1L;

    public MeshHeadingTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "DescriptorName"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "QualifierName"),
    };


    /**
     * Gets the "DescriptorName" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DescriptorNameType getDescriptorName() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DescriptorNameType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DescriptorNameType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "DescriptorName" element
     */
    @Override
    public void setDescriptorName(gov.nih.nlm.ncbi.eutils.DescriptorNameType descriptorName) {
        generatedSetterHelperImpl(descriptorName, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "DescriptorName" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DescriptorNameType addNewDescriptorName() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DescriptorNameType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DescriptorNameType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Gets a List of "QualifierName" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.QualifierNameType> getQualifierNameList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getQualifierNameArray,
                this::setQualifierNameArray,
                this::insertNewQualifierName,
                this::removeQualifierName,
                this::sizeOfQualifierNameArray
            );
        }
    }

    /**
     * Gets array of all "QualifierName" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.QualifierNameType[] getQualifierNameArray() {
        return getXmlObjectArray(PROPERTY_QNAME[1], new gov.nih.nlm.ncbi.eutils.QualifierNameType[0]);
    }

    /**
     * Gets ith "QualifierName" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.QualifierNameType getQualifierNameArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.QualifierNameType target = null;
            target = (gov.nih.nlm.ncbi.eutils.QualifierNameType)get_store().find_element_user(PROPERTY_QNAME[1], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "QualifierName" element
     */
    @Override
    public int sizeOfQualifierNameArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    /**
     * Sets array of all "QualifierName" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setQualifierNameArray(gov.nih.nlm.ncbi.eutils.QualifierNameType[] qualifierNameArray) {
        check_orphaned();
        arraySetterHelper(qualifierNameArray, PROPERTY_QNAME[1]);
    }

    /**
     * Sets ith "QualifierName" element
     */
    @Override
    public void setQualifierNameArray(int i, gov.nih.nlm.ncbi.eutils.QualifierNameType qualifierName) {
        generatedSetterHelperImpl(qualifierName, PROPERTY_QNAME[1], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "QualifierName" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.QualifierNameType insertNewQualifierName(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.QualifierNameType target = null;
            target = (gov.nih.nlm.ncbi.eutils.QualifierNameType)get_store().insert_element_user(PROPERTY_QNAME[1], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "QualifierName" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.QualifierNameType addNewQualifierName() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.QualifierNameType target = null;
            target = (gov.nih.nlm.ncbi.eutils.QualifierNameType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Removes the ith "QualifierName" element
     */
    @Override
    public void removeQualifierName(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }
}
