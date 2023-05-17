/*
 * An XML document type.
 * Localname: MedlineCitationSet
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * A document containing one MedlineCitationSet(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public class MedlineCitationSetDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument {
    private static final long serialVersionUID = 1L;

    public MedlineCitationSetDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "MedlineCitationSet"),
    };


    /**
     * Gets the "MedlineCitationSet" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet getMedlineCitationSet() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "MedlineCitationSet" element
     */
    @Override
    public void setMedlineCitationSet(gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet medlineCitationSet) {
        generatedSetterHelperImpl(medlineCitationSet, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "MedlineCitationSet" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet addNewMedlineCitationSet() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
    /**
     * An XML MedlineCitationSet(@http://www.ncbi.nlm.nih.gov/eutils).
     *
     * This is a complex type.
     */
    public static class MedlineCitationSetImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet {
        private static final long serialVersionUID = 1L;

        public MedlineCitationSetImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType);
        }

        private static final QName[] PROPERTY_QNAME = {
            new QName("http://www.ncbi.nlm.nih.gov/eutils", "MedlineCitation"),
            new QName("http://www.ncbi.nlm.nih.gov/eutils", "DeleteCitation"),
        };


        /**
         * Gets a List of "MedlineCitation" elements
         */
        @Override
        public java.util.List<gov.nih.nlm.ncbi.eutils.MedlineCitationType> getMedlineCitationList() {
            synchronized (monitor()) {
                check_orphaned();
                return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                    this::getMedlineCitationArray,
                    this::setMedlineCitationArray,
                    this::insertNewMedlineCitation,
                    this::removeMedlineCitation,
                    this::sizeOfMedlineCitationArray
                );
            }
        }

        /**
         * Gets array of all "MedlineCitation" elements
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.MedlineCitationType[] getMedlineCitationArray() {
            return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.MedlineCitationType[0]);
        }

        /**
         * Gets ith "MedlineCitation" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.MedlineCitationType getMedlineCitationArray(int i) {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.MedlineCitationType target = null;
                target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType)get_store().find_element_user(PROPERTY_QNAME[0], i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }

        /**
         * Returns number of "MedlineCitation" element
         */
        @Override
        public int sizeOfMedlineCitationArray() {
            synchronized (monitor()) {
                check_orphaned();
                return get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        /**
         * Sets array of all "MedlineCitation" element  WARNING: This method is not atomicaly synchronized.
         */
        @Override
        public void setMedlineCitationArray(gov.nih.nlm.ncbi.eutils.MedlineCitationType[] medlineCitationArray) {
            check_orphaned();
            arraySetterHelper(medlineCitationArray, PROPERTY_QNAME[0]);
        }

        /**
         * Sets ith "MedlineCitation" element
         */
        @Override
        public void setMedlineCitationArray(int i, gov.nih.nlm.ncbi.eutils.MedlineCitationType medlineCitation) {
            generatedSetterHelperImpl(medlineCitation, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
        }

        /**
         * Inserts and returns a new empty value (as xml) as the ith "MedlineCitation" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.MedlineCitationType insertNewMedlineCitation(int i) {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.MedlineCitationType target = null;
                target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
                return target;
            }
        }

        /**
         * Appends and returns a new empty value (as xml) as the last "MedlineCitation" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.MedlineCitationType addNewMedlineCitation() {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.MedlineCitationType target = null;
                target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType)get_store().add_element_user(PROPERTY_QNAME[0]);
                return target;
            }
        }

        /**
         * Removes the ith "MedlineCitation" element
         */
        @Override
        public void removeMedlineCitation(int i) {
            synchronized (monitor()) {
                check_orphaned();
                get_store().remove_element(PROPERTY_QNAME[0], i);
            }
        }

        /**
         * Gets the "DeleteCitation" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.DeleteCitationType getDeleteCitation() {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.DeleteCitationType target = null;
                target = (gov.nih.nlm.ncbi.eutils.DeleteCitationType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
                return (target == null) ? null : target;
            }
        }

        /**
         * True if has "DeleteCitation" element
         */
        @Override
        public boolean isSetDeleteCitation() {
            synchronized (monitor()) {
                check_orphaned();
                return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
            }
        }

        /**
         * Sets the "DeleteCitation" element
         */
        @Override
        public void setDeleteCitation(gov.nih.nlm.ncbi.eutils.DeleteCitationType deleteCitation) {
            generatedSetterHelperImpl(deleteCitation, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }

        /**
         * Appends and returns a new empty "DeleteCitation" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.DeleteCitationType addNewDeleteCitation() {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.DeleteCitationType target = null;
                target = (gov.nih.nlm.ncbi.eutils.DeleteCitationType)get_store().add_element_user(PROPERTY_QNAME[1]);
                return target;
            }
        }

        /**
         * Unsets the "DeleteCitation" element
         */
        @Override
        public void unsetDeleteCitation() {
            synchronized (monitor()) {
                check_orphaned();
                get_store().remove_element(PROPERTY_QNAME[1], 0);
            }
        }
    }
}
