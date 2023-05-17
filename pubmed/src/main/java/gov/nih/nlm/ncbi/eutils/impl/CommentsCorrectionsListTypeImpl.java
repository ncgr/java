/*
 * XML Type:  CommentsCorrectionsListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML CommentsCorrectionsListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class CommentsCorrectionsListTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType {
    private static final long serialVersionUID = 1L;

    public CommentsCorrectionsListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "CommentsCorrections"),
    };


    /**
     * Gets a List of "CommentsCorrections" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType> getCommentsCorrectionsList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getCommentsCorrectionsArray,
                this::setCommentsCorrectionsArray,
                this::insertNewCommentsCorrections,
                this::removeCommentsCorrections,
                this::sizeOfCommentsCorrectionsArray
            );
        }
    }

    /**
     * Gets array of all "CommentsCorrections" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType[] getCommentsCorrectionsArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType[0]);
    }

    /**
     * Gets ith "CommentsCorrections" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType getCommentsCorrectionsArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "CommentsCorrections" element
     */
    @Override
    public int sizeOfCommentsCorrectionsArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "CommentsCorrections" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setCommentsCorrectionsArray(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType[] commentsCorrectionsArray) {
        check_orphaned();
        arraySetterHelper(commentsCorrectionsArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "CommentsCorrections" element
     */
    @Override
    public void setCommentsCorrectionsArray(int i, gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType commentsCorrections) {
        generatedSetterHelperImpl(commentsCorrections, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "CommentsCorrections" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType insertNewCommentsCorrections(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "CommentsCorrections" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType addNewCommentsCorrections() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "CommentsCorrections" element
     */
    @Override
    public void removeCommentsCorrections(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
