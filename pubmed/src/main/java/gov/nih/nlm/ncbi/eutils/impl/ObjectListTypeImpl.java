/*
 * XML Type:  ObjectListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ObjectListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ObjectListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class ObjectListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.ObjectListType {
    private static final long serialVersionUID = 1L;

    public ObjectListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Object"),
    };


    /**
     * Gets a List of "Object" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.ObjectType> getObjectList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getObjectArray,
                this::setObjectArray,
                this::insertNewObject,
                this::removeObject,
                this::sizeOfObjectArray
            );
        }
    }

    /**
     * Gets array of all "Object" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ObjectType[] getObjectArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.ObjectType[0]);
    }

    /**
     * Gets ith "Object" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ObjectType getObjectArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ObjectType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ObjectType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Object" element
     */
    @Override
    public int sizeOfObjectArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Object" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setObjectArray(gov.nih.nlm.ncbi.eutils.ObjectType[] objectArray) {
        check_orphaned();
        arraySetterHelper(objectArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Object" element
     */
    @Override
    public void setObjectArray(int i, gov.nih.nlm.ncbi.eutils.ObjectType object) {
        generatedSetterHelperImpl(object, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Object" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ObjectType insertNewObject(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ObjectType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ObjectType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Object" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ObjectType addNewObject() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ObjectType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ObjectType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Object" element
     */
    @Override
    public void removeObject(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
