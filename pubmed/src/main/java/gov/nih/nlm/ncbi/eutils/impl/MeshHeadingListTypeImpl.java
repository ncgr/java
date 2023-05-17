/*
 * XML Type:  MeshHeadingListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MeshHeadingListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML MeshHeadingListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class MeshHeadingListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.MeshHeadingListType {
    private static final long serialVersionUID = 1L;

    public MeshHeadingListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "MeshHeading"),
    };


    /**
     * Gets a List of "MeshHeading" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.MeshHeadingType> getMeshHeadingList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getMeshHeadingArray,
                this::setMeshHeadingArray,
                this::insertNewMeshHeading,
                this::removeMeshHeading,
                this::sizeOfMeshHeadingArray
            );
        }
    }

    /**
     * Gets array of all "MeshHeading" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MeshHeadingType[] getMeshHeadingArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.MeshHeadingType[0]);
    }

    /**
     * Gets ith "MeshHeading" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MeshHeadingType getMeshHeadingArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MeshHeadingType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MeshHeadingType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "MeshHeading" element
     */
    @Override
    public int sizeOfMeshHeadingArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "MeshHeading" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setMeshHeadingArray(gov.nih.nlm.ncbi.eutils.MeshHeadingType[] meshHeadingArray) {
        check_orphaned();
        arraySetterHelper(meshHeadingArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "MeshHeading" element
     */
    @Override
    public void setMeshHeadingArray(int i, gov.nih.nlm.ncbi.eutils.MeshHeadingType meshHeading) {
        generatedSetterHelperImpl(meshHeading, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "MeshHeading" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MeshHeadingType insertNewMeshHeading(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MeshHeadingType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MeshHeadingType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "MeshHeading" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MeshHeadingType addNewMeshHeading() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MeshHeadingType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MeshHeadingType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "MeshHeading" element
     */
    @Override
    public void removeMeshHeading(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
