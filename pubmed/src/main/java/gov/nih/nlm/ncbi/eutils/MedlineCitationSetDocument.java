/*
 * An XML document type.
 * Localname: MedlineCitationSet
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one MedlineCitationSet(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface MedlineCitationSetDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlinecitationsetfd02doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "MedlineCitationSet" element
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet getMedlineCitationSet();

    /**
     * Sets the "MedlineCitationSet" element
     */
    void setMedlineCitationSet(gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet medlineCitationSet);

    /**
     * Appends and returns a new empty "MedlineCitationSet" element
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet addNewMedlineCitationSet();

    /**
     * An XML MedlineCitationSet(@http://www.ncbi.nlm.nih.gov/eutils).
     *
     * This is a complex type.
     */
    public interface MedlineCitationSet extends org.apache.xmlbeans.XmlObject {
        ElementFactory<gov.nih.nlm.ncbi.eutils.MedlineCitationSetDocument.MedlineCitationSet> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlinecitationset16d9elemtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        /**
         * Gets a List of "MedlineCitation" elements
         */
        java.util.List<gov.nih.nlm.ncbi.eutils.MedlineCitationType> getMedlineCitationList();

        /**
         * Gets array of all "MedlineCitation" elements
         */
        gov.nih.nlm.ncbi.eutils.MedlineCitationType[] getMedlineCitationArray();

        /**
         * Gets ith "MedlineCitation" element
         */
        gov.nih.nlm.ncbi.eutils.MedlineCitationType getMedlineCitationArray(int i);

        /**
         * Returns number of "MedlineCitation" element
         */
        int sizeOfMedlineCitationArray();

        /**
         * Sets array of all "MedlineCitation" element
         */
        void setMedlineCitationArray(gov.nih.nlm.ncbi.eutils.MedlineCitationType[] medlineCitationArray);

        /**
         * Sets ith "MedlineCitation" element
         */
        void setMedlineCitationArray(int i, gov.nih.nlm.ncbi.eutils.MedlineCitationType medlineCitation);

        /**
         * Inserts and returns a new empty value (as xml) as the ith "MedlineCitation" element
         */
        gov.nih.nlm.ncbi.eutils.MedlineCitationType insertNewMedlineCitation(int i);

        /**
         * Appends and returns a new empty value (as xml) as the last "MedlineCitation" element
         */
        gov.nih.nlm.ncbi.eutils.MedlineCitationType addNewMedlineCitation();

        /**
         * Removes the ith "MedlineCitation" element
         */
        void removeMedlineCitation(int i);

        /**
         * Gets the "DeleteCitation" element
         */
        gov.nih.nlm.ncbi.eutils.DeleteCitationType getDeleteCitation();

        /**
         * True if has "DeleteCitation" element
         */
        boolean isSetDeleteCitation();

        /**
         * Sets the "DeleteCitation" element
         */
        void setDeleteCitation(gov.nih.nlm.ncbi.eutils.DeleteCitationType deleteCitation);

        /**
         * Appends and returns a new empty "DeleteCitation" element
         */
        gov.nih.nlm.ncbi.eutils.DeleteCitationType addNewDeleteCitation();

        /**
         * Unsets the "DeleteCitation" element
         */
        void unsetDeleteCitation();
    }
}
