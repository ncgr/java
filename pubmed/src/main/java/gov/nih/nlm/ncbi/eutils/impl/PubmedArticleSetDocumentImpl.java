/*
 * An XML document type.
 * Localname: PubmedArticleSet
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * A document containing one PubmedArticleSet(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public class PubmedArticleSetDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument {
    private static final long serialVersionUID = 1L;

    public PubmedArticleSetDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PubmedArticleSet"),
    };


    /**
     * Gets the "PubmedArticleSet" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet getPubmedArticleSet() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "PubmedArticleSet" element
     */
    @Override
    public void setPubmedArticleSet(gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet pubmedArticleSet) {
        generatedSetterHelperImpl(pubmedArticleSet, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "PubmedArticleSet" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet addNewPubmedArticleSet() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet target = null;
            target = (gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
    /**
     * An XML PubmedArticleSet(@http://www.ncbi.nlm.nih.gov/eutils).
     *
     * This is a complex type.
     */
    public static class PubmedArticleSetImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument.PubmedArticleSet {
        private static final long serialVersionUID = 1L;

        public PubmedArticleSetImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType);
        }

        private static final QName[] PROPERTY_QNAME = {
            new QName("http://www.ncbi.nlm.nih.gov/eutils", "PubmedArticle"),
        };


        /**
         * Gets a List of "PubmedArticle" elements
         */
        @Override
        public java.util.List<gov.nih.nlm.ncbi.eutils.PubmedArticleType> getPubmedArticleList() {
            synchronized (monitor()) {
                check_orphaned();
                return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                    this::getPubmedArticleArray,
                    this::setPubmedArticleArray,
                    this::insertNewPubmedArticle,
                    this::removePubmedArticle,
                    this::sizeOfPubmedArticleArray
                );
            }
        }

        /**
         * Gets array of all "PubmedArticle" elements
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.PubmedArticleType[] getPubmedArticleArray() {
            return getXmlObjectArray(PROPERTY_QNAME[0], new gov.nih.nlm.ncbi.eutils.PubmedArticleType[0]);
        }

        /**
         * Gets ith "PubmedArticle" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.PubmedArticleType getPubmedArticleArray(int i) {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.PubmedArticleType target = null;
                target = (gov.nih.nlm.ncbi.eutils.PubmedArticleType)get_store().find_element_user(PROPERTY_QNAME[0], i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }

        /**
         * Returns number of "PubmedArticle" element
         */
        @Override
        public int sizeOfPubmedArticleArray() {
            synchronized (monitor()) {
                check_orphaned();
                return get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        /**
         * Sets array of all "PubmedArticle" element  WARNING: This method is not atomicaly synchronized.
         */
        @Override
        public void setPubmedArticleArray(gov.nih.nlm.ncbi.eutils.PubmedArticleType[] pubmedArticleArray) {
            check_orphaned();
            arraySetterHelper(pubmedArticleArray, PROPERTY_QNAME[0]);
        }

        /**
         * Sets ith "PubmedArticle" element
         */
        @Override
        public void setPubmedArticleArray(int i, gov.nih.nlm.ncbi.eutils.PubmedArticleType pubmedArticle) {
            generatedSetterHelperImpl(pubmedArticle, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
        }

        /**
         * Inserts and returns a new empty value (as xml) as the ith "PubmedArticle" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.PubmedArticleType insertNewPubmedArticle(int i) {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.PubmedArticleType target = null;
                target = (gov.nih.nlm.ncbi.eutils.PubmedArticleType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
                return target;
            }
        }

        /**
         * Appends and returns a new empty value (as xml) as the last "PubmedArticle" element
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.PubmedArticleType addNewPubmedArticle() {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.PubmedArticleType target = null;
                target = (gov.nih.nlm.ncbi.eutils.PubmedArticleType)get_store().add_element_user(PROPERTY_QNAME[0]);
                return target;
            }
        }

        /**
         * Removes the ith "PubmedArticle" element
         */
        @Override
        public void removePubmedArticle(int i) {
            synchronized (monitor()) {
                check_orphaned();
                get_store().remove_element(PROPERTY_QNAME[0], i);
            }
        }
    }
}
