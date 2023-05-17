/*
 * XML Type:  PersonalNameSubjectListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML PersonalNameSubjectListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class PersonalNameSubjectListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType {
    private static final long serialVersionUID = 1L;

    public PersonalNameSubjectListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PersonalNameSubject"),
    };


    /**
     * Gets a List of "PersonalNameSubject" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType> getPersonalNameSubjectList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getPersonalNameSubjectArray,
                this::setPersonalNameSubjectArray,
                this::insertNewPersonalNameSubject,
                this::removePersonalNameSubject,
                this::sizeOfPersonalNameSubjectArray
            );
        }
    }

    /**
     * Gets array of all "PersonalNameSubject" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType[] getPersonalNameSubjectArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType[0]);
    }

    /**
     * Gets ith "PersonalNameSubject" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType getPersonalNameSubjectArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "PersonalNameSubject" element
     */
    @Override
    public int sizeOfPersonalNameSubjectArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "PersonalNameSubject" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setPersonalNameSubjectArray(gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType[] personalNameSubjectArray) {
        check_orphaned();
        arraySetterHelper(personalNameSubjectArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "PersonalNameSubject" element
     */
    @Override
    public void setPersonalNameSubjectArray(int i, gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType personalNameSubject) {
        generatedSetterHelperImpl(personalNameSubject, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PersonalNameSubject" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType insertNewPersonalNameSubject(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "PersonalNameSubject" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType addNewPersonalNameSubject() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "PersonalNameSubject" element
     */
    @Override
    public void removePersonalNameSubject(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
